/*
 * Copyright (c) 2018 Stanislaw stasbar Baranski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *          __             __
 *    _____/ /_____ ______/ /_  ____ ______
 *   / ___/ __/ __ `/ ___/ __ \/ __ `/ ___/
 *  (__  ) /_/ /_/ (__  ) /_/ / /_/ / /
 * /____/\__/\__,_/____/_.___/\__,_/_/
 *            taxledger@stasbar.com
 */

package com.stasbar.taxledger

import com.stasbar.taxledger.completers.ActionsCompleter
import com.stasbar.taxledger.completers.TransactionsCompleter
import com.stasbar.taxledger.exceptions.ApiNotSetException
import com.stasbar.taxledger.exceptions.CredentialsException
import com.stasbar.taxledger.exceptions.IllegalDateRangeArgument
import com.stasbar.taxledger.exceptions.TooManyCredentialsException
import com.stasbar.taxledger.models.Credential
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.options.DateRange
import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.translations.Text
import com.stasbar.taxledger.writers.BitBayCsvReader
import com.stasbar.taxledger.writers.ConsoleWriter
import com.stasbar.taxledger.writers.CsvWriter
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.completer.ArgumentCompleter
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.terminal.TerminalBuilder
import java.nio.file.Paths
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

/**
 * Abstraction for both UNIX terminal and Windows Console
 */
val terminal = TerminalBuilder
        .builder()
        .dumb(true)
        .jansi(true)
        .build()!!

/**
 * In debug mode logs are printed onto console
 */
val DEBUG = false


/**
 * Set of all available exchanges.
 * We can threat this set of KClasses as set of kotlin objects (Singletons) with .objectInstance reflection method
 */
val exchanges = setOf(BitBay::class, Abucoins::class)

/**
 * Argument deque on which whole CLI is working on
 */
val args = ArrayDeque<String>()

fun main(cliArgs: Array<String>) {
    AnsiConsole.systemInstall()

    ConsoleWriter.printIntro()

    val credentials = PreferencesManager.load()

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


fun parseExchangeName(): Boolean {
    val buffer = StringBuilder()
    exchanges.map { it.simpleName }.joinTo(buffer)

    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(StringsCompleter(exchanges.map { it.objectInstance!!.name }.toList()))
            .appName("Tax Ledger")
            .build()

    val prompt = getString(Text.ENTER_EXCHANGE_NAME) + " [$buffer]: "
    var exchangeNameTry = ""
    try {
        exchangeNameTry = reader.readLine(prompt).trim().split(" ")[0]
    } catch (e: UserInterruptException) {
        ConsoleWriter.printExitMessage()
        System.exit(0)
    } catch (e: IllegalStateException) {
        ConsoleWriter.printExitMessage()
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

        for (credentialStep in exchange.credentials) {
            var correctValue: Boolean
            do {
                correctValue = try {
                    // Try add credential from argument queue
                    credentialStep.value = args.peekFirst()
                    args.pop()
                    true
                } catch (e: CredentialsException) {
                    // Failed, try from
                    promptUserForCredential(exchange, credentialStep)
                } catch (e: TooManyCredentialsException) {
                    // Failed, try from
                    promptUserForCredential(exchange, credentialStep)
                } catch (e: IllegalStateException) {
                    // Failed, try from
                    promptUserForCredential(exchange, credentialStep)
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


fun promptUserForCredential(exchange: Exchange<out ExchangeApi>, credentialStep: Credential): Boolean {
    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build()
    val prompt = ansi()
            .fgBright(exchange.color).a(exchange.name + "> ")
            .fgBrightYellow().a(credentialStep.name).a(">")
            .reset().toString()
    var stepAnswer = ""
    try {
        stepAnswer = reader.readLine(prompt)
    } catch (e: UserInterruptException) {
        ConsoleWriter.printExitMessage()
        System.exit(0)
    } catch (e: IllegalStateException) {
        ConsoleWriter.printExitMessage()
        System.exit(0)
    }
    try {
        credentialStep.value = stepAnswer.trim()
        exchange.addCredential(credentialStep)
        return true
    } catch (e: CredentialsException) {
        Logger.err(e.message)
    } catch (e: TooManyCredentialsException) {
        Logger.err(e.message)
    }
    return false
}

fun saveCredentials(supportedExchanges: Set<KClass<out Exchange<out ExchangeApi>>>) {
    PreferencesManager.save(supportedExchanges)
    Logger.info(getString(Text.CREDENTIALS_SAVED).format(PreferencesManager.credentials.absoluteFile))
}


fun parseAction() {
    for (action in Action.values()) {
        val actionLine = ansi().a("\n\t").a(String(Character.toChars(action.symbol))).a("  ").bold().a(getString(action.title)).boldOff()
        if (getString(action.description).isNotBlank())
            actionLine.a(" - ${getString(action.description)}")
        AnsiConsole.out.println(actionLine)
    }
    AnsiConsole.out.println()

    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(ArgumentCompleter(ActionsCompleter(), TransactionsCompleter()))
            .build()
    var line = ""
    try {
        line = reader.readLine(getString(Text.ACTION) + "> ")
    } catch (e: UserInterruptException) {
        ConsoleWriter.printExitMessage()
        System.exit(0)
    } catch (e: IllegalStateException) {
        ConsoleWriter.printExitMessage()
        System.exit(0)
    }
    line.split(" ").filter { it.isNotBlank() }.forEach { args.add(it) }

}


fun parseTransactionOptions(): TransactionsOptions {
    val option = TransactionsOptions()
    while (args.peekFirst() != null && args.peekFirst().contains("-")) {
        val argument = args.pollFirst()
        option.fileName.append(argument)

        if (tryToParseOneSideBoundDateRange(option.dateBefore, argument, args.peekFirst(), "-before", "-przed"))
            continue

        if (tryToParseOneSideBoundDateRange(option.dateAfter, argument, args.peekFirst(), "-after", "-po"))
            continue

        if (tryToParseDateRange(option.dateRange, argument, false))
            continue

        if (tryToParseOldBitBayHistory(option, argument, args.peekFirst()))
            continue

        when (argument.toLowerCase()) {
            "-showNonFiat".toLowerCase()
                , "-showNoneFiat".toLowerCase()
                , "-showNoFiat".toLowerCase() -> option.showNonFiat = true

            "-showNonEssential".toLowerCase()
                , "-showNoneEssential".toLowerCase()
                , "-showNoEssential".toLowerCase() -> option.showNonEssential = true

            "-all" -> {
                option.showNonEssential = true
                option.showNonFiat = true
            }
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

fun tryToParseOneSideBoundDateRange(sideDateRange: DateRange, argument: String?, nextArgument: String?, vararg synonyms: String): Boolean {
    return if (argument in synonyms && nextArgument != null && nextArgument.isNotBlank()) {
        val success = tryToParseDateRange(sideDateRange, nextArgument, true)
        if (success) args.pop()
        success
    } else false
}

fun tryToParseDateRange(dateRange: DateRange, argument: String, parameterArgument: Boolean): Boolean {
    var success = isLikelyToBeDateFormat(argument) && tryToParseExplicitDate(dateRange, argument, parameterArgument)
    if (!success)
        success = tryToParseImplicitDate(dateRange, argument)
    return success
}


fun isLikelyToBeDateFormat(candidate: String) = candidate.matches(Regex("-?([0-9]{1,2}).([0-9]{1,2}).([0-9]{4})"))
        || candidate.matches(Regex("-?([0-9]{1,2}).([0-9]{4})"))
        || candidate.matches(Regex("-?([0-9]{4})"))


fun tryToParseImplicitDate(dateRange: DateRange, argument: String): Boolean {
    val calendar = Date().toCalendar()
    when (argument.toLowerCase()) {
        "-today".toLowerCase() -> {
            dateRange.setToDay(calendar)
        }
        "-yesterday".toLowerCase() -> {
            calendar.roll(Calendar.DAY_OF_MONTH, -1)
            dateRange.setToDay(calendar)
        }
        "-thisMonth".toLowerCase() -> {
            dateRange.setToMonth(calendar)
        }
        "-prevMonth".toLowerCase() -> {
            calendar.roll(Calendar.MONTH, -1)
            dateRange.setToMonth(calendar)
        }
        "-thisYear".toLowerCase() -> {
            dateRange.setToYear(calendar)
        }
        "-prevYear".toLowerCase() -> {
            calendar.roll(Calendar.YEAR, -1)
            dateRange.setToYear(calendar)
        }
        else -> return false
    }
    return true
}


fun tryToParseExplicitDate(dateRange: DateRange, argument: String, parameterArgument: Boolean): Boolean {
    val parsePosition = ParsePosition(if (parameterArgument) 0 else 1)
    try {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")

        val calendar = dateFormat.parse(argument, parsePosition)
                ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
        dateRange.setToDay(calendar.toCalendar())

    } catch (e: Exception) {
        try {
            val dateFormat = SimpleDateFormat("MM.yyyy")
            val calendar = dateFormat.parse(argument, parsePosition)
                    ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
            dateRange.setToMonth(calendar.toCalendar())
        } catch (e: Exception) {
            try {
                val dateFormat = SimpleDateFormat("yyyy")
                dateFormat.isLenient = false
                val calendar = dateFormat.parse(argument, parsePosition)
                        ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
                dateRange.setToYear(calendar.toCalendar())
            } catch (e: ParseException) {
                Logger.err(e.message)
                return false
            } catch (e: IllegalDateRangeArgument) {
                //if(e.arg.length >=2 && e.arg[1].isDigit())
                Logger.err(e.message)
                return false
            }
        }
    }
    return true
}


fun tryToParseOldBitBayHistory(option: TransactionsOptions, argument: String, nextArgument: String?): Boolean {
    return if (validateOldBbArgument(argument, nextArgument)) {
        option.oldBitBayHistory = Paths.get(nextArgument).toFile()
        args.pop()
        true
    } else false

}

fun validateOldBbArgument(argument: String, nextArgument: String?): Boolean {
    Logger.d("argument: $argument nextArgument: $nextArgument")
    if (argument.toLowerCase() != "-oldbb") {
        return false
    }
    if (nextArgument == null || nextArgument.isBlank()) {
        Logger.err(getString(Text.COULD_NOT_FIND_PATH_ARGUMENT))
        return false
    }

    if (!Paths.get(nextArgument).toFile().exists()) {
        Logger.err(getString(Text.INVALID_OLDBB_CSV_PATH).format(nextArgument))
        return false
    }
    return true

}


fun performActions(action: String): Boolean {

    when (action.toUpperCase()) {
        getString(Action.TRANSACTIONS.title).toUpperCase(), Action.TRANSACTIONS.name -> {
            performTransactionsAction()
        }
        getString(Action.OPEN.title).toUpperCase(), Action.OPEN.name -> {
            performOpenFolder()
        }
        getString(Action.CONTACT.title).toUpperCase(), Action.CONTACT.name -> {
            AnsiConsole.out.println(Misc.contact)
        }
        getString(Action.EXCHANGES.title).toUpperCase(), Action.EXCHANGES.name -> {
            parseCredentials()
        }
        getString(Action.DONATE.title).toUpperCase(), Action.DONATE.name -> {
            ConsoleWriter.printDonate()
        }
        getString(Action.EXIT.title).toUpperCase(), Action.EXIT.name -> {
            ConsoleWriter.printExitMessage()
            System.exit(0)
        }
        else -> return false
    }

    return true
}

fun performOpenFolder() {
    val runtime = Runtime.getRuntime()
    val os = System.getProperty("os.name").toLowerCase()
    when {
        os.indexOf("mac") >= 0 -> {
            val command = "open ${PreferencesManager.workingDir.absolutePath}"
            Logger.d(command)
            runtime.exec(command)
        }
        os.indexOf("win") >= 0 -> {
            val command = "cmd /c start ${PreferencesManager.workingDir.absolutePath}"
            Logger.d(command)
            runtime.exec(command)
        }
        os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0 -> {
            Logger.err("Unsupported action on your OS $$os ")
        }
        os.indexOf("sunos") >= 0 -> {
            Logger.err("Unsupported action on your OS $$os ")
        }
    }
}

fun performTransactionsAction(): Boolean {
    val options = parseTransactionOptions()

    var transactions = ArrayList<Transaction>()
    val apis = exchanges
            .map { it.objectInstance!! }
            .filter {
                //if one exchange is not specified then pass all exchanges
                if (options.oneExchangeOnly == null) true
                else it == options.oneExchangeOnly
            }

    apis.parallelStream().forEach {
        try {

            val newTransactions: List<Transaction> = try {
                it.getApi().transactions()
            } catch (e: IllegalStateException) {
                println("Reconnecting to ${it.name}")
                try {
                    it.getApi().transactions()
                } catch (e: IllegalStateException) {
                    Logger.err("Failed $e")
                    emptyList()
                }
            }

            transactions.addAll(newTransactions)

        } catch (e: ApiNotSetException) {
            /* skip not set exchanges */
        }
    }

    options.oldBitBayHistory?.let { transactions.addAll(BitBayCsvReader.readCsv(it)) }

    transactions = ArrayList(transactions.filter { options.isInRange(it) })


    if (transactions.isEmpty()) {
        Logger.err(getString(Text.NO_OPERATIONS))
        return true
    }

    val comparator: Comparator<Transaction> = if (options.reverse)
        kotlin.Comparator { o1, o2 -> o1.time.compareTo(o2.time) }
    else kotlin.Comparator { o1, o2 -> o2.time.compareTo(o1.time) }

    transactions.sortWith(comparator)

    ConsoleWriter.printTransactions(transactions, options)
    ConsoleWriter.printSummary(transactions)
    Logger.info(getString(Text.LOAD_COMPLETE).format(transactions.size))
    CsvWriter.saveToFile(transactions, options)
    return true
}




