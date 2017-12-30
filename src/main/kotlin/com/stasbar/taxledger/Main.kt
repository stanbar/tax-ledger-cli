package com.stasbar.taxledger

import com.stasbar.taxledger.exceptions.ApiNotSetException
import com.stasbar.taxledger.exceptions.CredentialsException
import com.stasbar.taxledger.exceptions.TooManyCredentialsException
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.options.DateRange
import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.translations.Text
import org.fusesource.jansi.Ansi.ansi
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.terminal.TerminalBuilder
import java.util.*
import kotlin.reflect.KClass


val exchanges = setOf(BitBay::class, Abucoins::class)
val DEBUG = false

val terminal = TerminalBuilder.terminal()
val preferenceManager = PreferencesManager()

val args = ArrayDeque<String>()

private val BUNDLE_NAME = "com.stasbar.taxledger.translations.Text"
var resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.getDefault())

fun selectLanguage(lang: String) {
    when {
        Locale.forLanguageTag(lang) != null -> resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.forLanguageTag(lang))
        else -> throw IllegalStateException(getString(Text.UNKNOWN_LANGUAGE))
    }

}

fun getString(key: String) = resourceBundle.getString(key)

fun main(cliArgs: Array<String>) {
    Printer.printIntro()
    val credentials = loadCredentialsStrings()

    args.addAll(credentials)
    args.addAll(cliArgs)

    parseCredentials()

    while (true) {
        if (args.isEmpty())
            parseAction()
        while (!args.isEmpty()) {
            val success = performActions(args.pollFirst())
            if (success.not())
                Logger.err(getString(Text.WRONG_ACTION))
        }
    }


}


fun loadCredentialsStrings() = preferenceManager.load()


fun parseExchangeName(): Boolean {
    val buffer = StringBuilder()
    exchanges.map { it.simpleName }.joinTo(buffer)

    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(StringsCompleter(exchanges.map { it.objectInstance!!.name }.toList()))
            .appName("Tax-Ledger")
            .build()

    val prompt = getString(Text.ENTER_EXCHANGE_NAME) + " [$buffer]: "
    var exchangeNameTry = ""
    try {
        exchangeNameTry = reader.readLine(prompt).trim().split(" ")[0]
    } catch (e: UserInterruptException) {
        Printer.printExitMessage()
        System.exit(0)
    }
    for (exchange in exchanges)
        if (exchange.objectInstance!!.isNameOf(exchangeNameTry)) {
            args.add(exchange.objectInstance!!.name)
            return true
        }
    return false

}

fun isExchangeName(name: String) = exchanges.any { it.objectInstance!!.isNameOf(name) }

fun parseCredentials() {
    if (args.isEmpty() || !isExchangeName(args.peekFirst()))
        while (!parseExchangeName());


    while (args.peekFirst() != null
            && isExchangeName(args.peekFirst())) {
        val exchange = exchangeByName(args.peekFirst()).objectInstance!!
        args.pop() //Now we are sure that this is correct exchange name so we can safely pop() and process

        for (step in exchange.credentialsSteps) {
            var correctValue: Boolean
            do {
                correctValue = try {
                    // Try add credential from argument queue
                    exchange.addCredential(args.peekFirst())
                    args.pop()
                    true
                } catch (e: CredentialsException) {
                    // Failed, try from
                    promptUserForCredential(exchange, step)
                } catch (e: TooManyCredentialsException) {
                    // Failed, try from
                    promptUserForCredential(exchange, step)
                } catch (e: IllegalStateException) {
                    // Failed, try from
                    promptUserForCredential(exchange, step)
                }

            } while (!correctValue)


        }
        try {
            exchange.getApi() // invoke lazy init api
            Logger.info(getString(Text.ADDED_CREDENTIALS) + exchange.name)
        } catch (e: IllegalStateException) {
            Logger.err(e.message)
        }


    }

    saveCredentials(exchanges)

}


fun exchangeByName(pollFirst: String) = exchanges.first { it.objectInstance!!.isNameOf(pollFirst) }


fun promptUserForCredential(exchange: Exchange<ExchangeApi>, step: String): Boolean {
    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build()
    val prompt = ansi()
            .fgBright(exchange.color).a(exchange.name + "> ")
            .fgBrightYellow().a(step).a(">")
            .reset()
    var stepAnswer = ""
    try {
        stepAnswer = reader.readLine(prompt.toString())
    } catch (e: UserInterruptException) {
        Printer.printExitMessage()
        System.exit(0)
    }
    try {

        exchange.addCredential(stepAnswer.trim())
        return true
    } catch (e: CredentialsException) {
        Logger.err(e.message)
    } catch (e: TooManyCredentialsException) {
        Logger.err(e.message)
    }
    return false
}

fun saveCredentials(supportedExchanges: Set<KClass<out Exchange<ExchangeApi>>>) {
    preferenceManager.save(supportedExchanges)
    Logger.info(getString(Text.CREDENTIALS_SAVED).format(preferenceManager.file.absoluteFile))
}


fun parseAction() {
    for (action in Action.values()) {
        val actionLine = ansi().a("\n\t").a(String(Character.toChars(action.symbol))).bold().a(getString(action.title)).boldOff()
        if (getString(action.description).isNotBlank())
            actionLine.a(" - ${getString(action.description)}")
        println(actionLine)
    }
    println()

    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(StringsCompleter(Action.values().map { getString(it.title).toLowerCase() }))
            .build()
    var line = ""
    try {
        line = reader.readLine(getString(Text.ACTION) + "> ")
    } catch (e: UserInterruptException) {
        Printer.printExitMessage()
        System.exit(0)
    }
    line.split(" ").filter { it.isNotBlank() }.forEach { args.add(it) }

}


fun performActions(action: String): Boolean {

    when (action.toUpperCase()) {
        getString(Action.TRANSACTIONS.title).toUpperCase(), Action.TRANSACTIONS.name -> {
            val options = parseTransactionOptions()

            val transactions = ArrayList<Transaction>()
            val apis = exchanges
                    .map { it.objectInstance!! }
                    .filter {
                        //if one exchange is not specified then pass all exchanges
                        if (options.oneExchangeOnly == null) true
                        else it == options.oneExchangeOnly
                    }

            apis.parallelStream().forEach {
                try {
                    val newTransactions = it.getApi()
                            .transactions()
                            .filter { options.dateRange.isInRange(it) }
                            .toList()
                    transactions.addAll(newTransactions)
                } catch (e: ApiNotSetException) {
                    /* skip not set exchanges */
                }
            }

            val comparator: Comparator<Transaction> = if (options.reverse)
                kotlin.Comparator { o1, o2 -> o1.time.compareTo(o2.time) }
            else kotlin.Comparator { o1, o2 -> o2.time.compareTo(o1.time) }

            transactions.sortWith(comparator)

            Printer.printTransactions(transactions)
            Printer.printSummary(transactions)
            Logger.info(getString(Text.LOAD_COMPLETE).format(transactions.size))
            CsvManager.saveToFile(transactions, CsvManager.fileName(options.fileName.toString()))
        }
        getString(Action.CONTACT.title).toUpperCase(), Action.CONTACT.name -> {
            println(Misc.contact)
        }
        getString(Action.EXCHANGES.title).toUpperCase(), Action.EXCHANGES.name -> {
            parseCredentials()
        }
        getString(Action.DONATE.title).toUpperCase(), Action.DONATE.name -> {
            Printer.printDonate()
        }
        getString(Action.EXIT.title).toUpperCase(), Action.EXIT.name -> {
            Printer.printExitMessage()
            System.exit(0)

        }
        else -> return false
    }

    return true
}


fun parseTransactionOptions(): TransactionsOptions {
    val option = TransactionsOptions()
    while (args.peekFirst() != null && args.peekFirst().contains("-")) {
        val argument = args.pollFirst()
        option.fileName.append(argument)
        when (argument.toLowerCase()) {
            "-thisWeek".toLowerCase() -> option.dateRange = DateRange.THIS_WEEK
            "-prevWeek".toLowerCase() -> option.dateRange = DateRange.PREV_WEEK
            "-thisMonth".toLowerCase() -> option.dateRange = DateRange.THIS_MONTH
            "-prevMonth".toLowerCase() -> option.dateRange = DateRange.PREV_MONTH
            "-thisYear".toLowerCase() -> option.dateRange = DateRange.THIS_YEAR
            "-prevYear".toLowerCase() -> option.dateRange = DateRange.PREV_YEAR
            "-reverse".toLowerCase() -> option.reverse = true
            "-bitbayOnly".toLowerCase(), "-bbOnly".toLowerCase()
                , "-onlyBitbay".toLowerCase(), "-onlybb".toLowerCase() -> option.oneExchangeOnly = BitBay::class.objectInstance!!
            "-abucoinsOnly".toLowerCase(), "-abuOnly".toLowerCase()
                , "-onlyAbucoins".toLowerCase(), "-onlyAbu".toLowerCase() -> option.oneExchangeOnly = Abucoins::class.objectInstance!!
            else -> Logger.err(getString(Text.Exceptions.INVALID_ARG).format(argument))
        }
    }
    return option
}








