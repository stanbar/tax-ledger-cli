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

import com.stasbar.taxledger.exceptions.ApiNotSetException
import com.stasbar.taxledger.exceptions.CredentialsException
import com.stasbar.taxledger.exceptions.IllegalDateRangeArgument
import com.stasbar.taxledger.exceptions.TooManyCredentialsException
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.translations.Text
import com.stasbar.taxledger.writers.ConsoleWriter
import com.stasbar.taxledger.writers.CsvWriter
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import org.jline.reader.LineReaderBuilder
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.terminal.TerminalBuilder
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
        .jansi(true)
        .build()!!

/**
 * In debug mode logs are printed onto console
 */
val DEBUG = false

/**
 * Credentials file manager
 */
val preferenceManager = PreferencesManager()

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

    val credentials = preferenceManager.load()

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
        AnsiConsole.out.println(actionLine)
    }
    AnsiConsole.out.println()

    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(StringsCompleter(Action.values().map { getString(it.title).toLowerCase() }))
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


        if (tryToParseDateRange(option, argument))
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

fun isLikelyToBeDateFormat(candidate: String) = candidate.matches(Regex("-([0-9]{2}).([0-9]{2}).([0-9]{4})"))
        || candidate.matches(Regex("-([0-9]{2}).([0-9]{4})"))
        || candidate.matches(Regex("-([0-9]{4})"))

fun tryToParseDateRange(options: TransactionsOptions, argument: String): Boolean {
    var success = isLikelyToBeDateFormat(argument) && tryToParseExplicitDate(options, argument)
    if (!success)
        success = tryToParseImplicitDate(options, argument)
    return success
}

fun tryToParseImplicitDate(options: TransactionsOptions, argument: String): Boolean {
    val calendar = Date().toCalendar()
    when (argument.toLowerCase()) {
        "-today".toLowerCase() -> {
            options.dateRange.setToDay(calendar)
        }
        "-yesterday".toLowerCase() -> {
            calendar.roll(Calendar.DAY_OF_MONTH, -1)
            options.dateRange.setToDay(calendar)
        }
        "-thisMonth".toLowerCase() -> {
            options.dateRange.setToMonth(calendar)
        }
        "-prevMonth".toLowerCase() -> {
            calendar.roll(Calendar.MONTH, -1)
            options.dateRange.setToMonth(calendar)
        }
        "-thisYear".toLowerCase() -> {
            options.dateRange.setToYear(calendar)
        }
        "-prevYear".toLowerCase() -> {
            calendar.roll(Calendar.YEAR, -1)
            options.dateRange.setToYear(calendar)
        }
        else -> return false
    }
    return true
}


fun tryToParseExplicitDate(options: TransactionsOptions, argument: String): Boolean {
    val parsePosition = ParsePosition(1)
    try {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy")

        val calendar = dateFormat.parse(argument, parsePosition)
                ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)

        options.dateRange.setToDay(calendar.toCalendar())

    } catch (e: Exception) {
        try {
            val dateFormat = SimpleDateFormat("MM.yyyy")

            val calendar = dateFormat.parse(argument, parsePosition)
                    ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
            options.dateRange.setToMonth(calendar.toCalendar())
        } catch (e: Exception) {
            try {
                val dateFormat = SimpleDateFormat("yyyy")
                dateFormat.isLenient = false
                val calendar = dateFormat.parse(argument, parsePosition)
                        ?: throw IllegalDateRangeArgument(argument, parsePosition.errorIndex)
                options.dateRange.setToYear(calendar.toCalendar())
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


fun performActions(action: String): Boolean {

    when (action.toUpperCase()) {
        getString(Action.TRANSACTIONS.title).toUpperCase(), Action.TRANSACTIONS.name -> {
            performTransactionsAction()
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

fun performTransactionsAction(): Boolean {
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

            val newTransactions = try {
                it.getApi().transactions()
            } catch (e: IllegalStateException) {
                println("Reconnecting to ${it.name}")
                try {
                    it.getApi().transactions()
                } catch (e: IllegalStateException) {
                    Logger.err("Failed $e")
                    null
                }
            }
            if (newTransactions != null)
                transactions.addAll(newTransactions.filter { options.dateRange.isInRange(it) })
        } catch (e: ApiNotSetException) {
            /* skip not set exchanges */
        }
    }
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




