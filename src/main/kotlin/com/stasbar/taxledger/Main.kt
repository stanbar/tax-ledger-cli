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
import com.stasbar.taxledger.models.Credential
import com.stasbar.taxledger.models.Transaction
import com.stasbar.taxledger.models.Transactionable
import com.stasbar.taxledger.options.TransactionsOptions
import com.stasbar.taxledger.translations.Text
import com.stasbar.taxledger.writers.ConsoleWriter
import com.stasbar.taxledger.writers.CsvWriter
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole
import org.jline.reader.LineReaderBuilder
import org.jline.reader.impl.completer.ArgumentCompleter
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.terminal.TerminalBuilder
import java.awt.Desktop
import java.net.URI
import java.util.*

/**
 * Abstraction for both UNIX terminal and Windows Console
 */
private val terminal = TerminalBuilder
        .builder()
        .dumb(true)
        .jansi(true)
        .build()!!

/**
 * In debug mode logs are printed onto console
 */
var DEBUG = false


/**
 * Set of all available exchanges.
 * We can threat this set of KClasses as set of kotlin objects (Singletons) with .objectInstance reflection method
 */
private val exchanges = setOf(
        BitBay::class,
        Abucoins::class)

/**
 * Argument deque on which whole CLI is working on
 */
private val args = ArrayDeque<String>()


fun main(cliArgs: Array<String>) {
    AnsiConsole.systemInstall()
    Misc.printIntro()

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

fun injectArgs(newArgs: ArrayDeque<String>) = args.addAll(newArgs)

fun isExchangeName(name: String) = exchanges.any { it.objectInstance!!.isNameOf(name) }

fun exchangeByName(pollFirst: String) = exchanges.first { it.objectInstance!!.isNameOf(pollFirst) }

fun parseExchangeName(): Boolean {
    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .completer(StringsCompleter(exchanges.map { it.objectInstance!!.name }.toList()))
            .appName("Tax Ledger")
            .build()

    val exchangesString = exchanges.map { it.simpleName }.joinToString()
    val prompt = getString(Text.ENTER_EXCHANGE_NAME) + " [$exchangesString]: "
    val exchangeName = try {
        reader.readLine(prompt).trim().split(" ")[0]
    } catch (e: Exception) {
        Misc.printExitMessage()
        System.exit(0)
        null
    }
    for (exchange in exchanges)
        if (exchange.objectInstance!!.isNameOf(exchangeName!!)) {
            args.add(exchange.objectInstance!!.name)
            return true
        }
    return false

}

fun parseCredentials() {
    if (args.isEmpty() || !isExchangeName(args.peekFirst()))
        while (!parseExchangeName());

    while (args.peekFirst() != null
            && isExchangeName(args.peekFirst())) {
        val exchange = exchangeByName(args.peekFirst()).objectInstance!!
        args.pop() //Now we are sure that this is correct exchange name so we can safely pop() and process

        exchange.credentials.forEach { credential ->
            var correctValue: Boolean
            do {
                correctValue = try {
                    // Try add credential from argument queue
                    credential.value = args.peekFirst()
                    args.pop()
                    true
                } catch (e: Exception) {
                    // Failed, try from
                    promptUserForCredential(exchange, credential)
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
    PreferencesManager.save(exchanges)
}

fun promptUserForCredential(exchange: Exchange<out ExchangeApi<Transactionable, Transactionable>>, credentialStep: Credential): Boolean {
    val reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .build()
    val prompt = ansi()
            .fgBright(exchange.color).a(exchange.name + "> ")
            .fgBrightYellow().a(credentialStep.name).a(">")
            .reset().toString()
    val stepAnswer = try {
        reader.readLine(prompt)
    } catch (e: Exception) {
        Misc.printExitMessage()
        System.exit(0)
        null
    }
    try {
        credentialStep.value = stepAnswer!!.trim()
        exchange.addCredential(credentialStep)
        return true
    } catch (e: Exception) {
        Logger.err(e.message)
    }
    return false
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
    val line = try {
        reader.readLine(getString(Text.ACTION) + "> ")
    } catch (e: Exception) {
        Misc.printExitMessage()
        System.exit(0)
        null
    }
    line!!.split(" ").filter { it.isNotBlank() }.forEach { args.add(it) }

}


fun performActions(action: String): Boolean {
    when (action.toUpperCase()) {
        getString(Action.TRANSACTIONS.title).toUpperCase(), Action.TRANSACTIONS.name -> performTransactionsAction()
        getString(Action.OPEN.title).toUpperCase(), Action.OPEN.name -> performOpenFolder()
        getString(Action.CONTACT.title).toUpperCase(), Action.CONTACT.name -> AnsiConsole.out.println(Misc.contact)
        getString(Action.EXCHANGES.title).toUpperCase(), Action.EXCHANGES.name -> parseCredentials()
        getString(Action.DONATE.title).toUpperCase(), Action.DONATE.name -> Misc.printDonate()
        getString(Action.EXIT.title).toUpperCase(), Action.EXIT.name -> {
            Misc.printExitMessage()
            System.exit(0)
        }
        getString(Action.FOLLOW.title).toUpperCase(), Action.FOLLOW.name -> {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI(Constants.facebook))
                Desktop.getDesktop().browse(URI(Constants.twitter))
            }
        }
        "DEBUG" -> DEBUG = true
        else -> return false
    }
    return true
}


fun performTransactionsAction(): Boolean {
    val options = TransactionsOptions.parse(args, exchanges)

    var transactions: MutableList<Transaction> = arrayListOf<Transaction>()
    val apis = exchanges
            .map { it.objectInstance!! }
            .filter {
                //if one exchange is not specified then pass all exchanges
                if (options.oneExchangeOnly == null) true
                else it == options.oneExchangeOnly
            }


    apis.parallelStream().forEach {
        try {
            transactions.addAll(it.getApi().transactions().map { it.toTransaction() })
            transactions.addAll(it.getApi().fees().map { it.toTransaction() })
            transactions.addAll(it.getApi().depositsAndWithdraws().map { it.toTransaction() })
        } catch (e: ApiNotSetException) {
            /* skip not set exchanges */
        }
    }

    if (options.fifo)
        transactions = Transformations.recalculateWithFifo(transactions)

    transactions = transactions.filter { options.isInRange(it) }.toMutableList()

    if (transactions.isEmpty()) {
        Logger.err(getString(Text.NO_OPERATIONS))
        return true
    }

    val comparator: Comparator<Transaction> = if (options.reverse)
        kotlin.Comparator { o1, o2 -> o1.time.compareTo(o2.time) }
    else kotlin.Comparator { o1, o2 -> o2.time.compareTo(o1.time) }

    transactions = transactions.toMutableList()
    transactions.sortWith(comparator)

    val consoleWriter = ConsoleWriter(transactions)
    consoleWriter.printTransactions(options)
    consoleWriter.printSummary()

    Logger.info(getString(Text.LOAD_COMPLETE).format(transactions.size))
    val csvWriter = CsvWriter(transactions)
    csvWriter.saveToFile(options)
    return true
}




