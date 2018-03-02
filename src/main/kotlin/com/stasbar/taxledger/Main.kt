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
import com.stasbar.taxledger.exceptions.TooManyCredentialsException
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
import org.jline.reader.UserInterruptException
import org.jline.reader.impl.completer.ArgumentCompleter
import org.jline.reader.impl.completer.StringsCompleter
import org.jline.terminal.TerminalBuilder
import java.awt.Desktop
import java.net.URI
import java.util.*
import kotlin.reflect.KClass

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

fun injectArgs(newArgs: ArrayDeque<String>) {
    args.addAll(newArgs)
}

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
        Misc.printExitMessage()
        System.exit(0)
    } catch (e: IllegalStateException) {
        Misc.printExitMessage()
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


fun promptUserForCredential(exchange: Exchange<out ExchangeApi<Transactionable, Transactionable>>, credentialStep: Credential): Boolean {
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
        Misc.printExitMessage()
        System.exit(0)
    } catch (e: IllegalStateException) {
        Misc.printExitMessage()
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

fun saveCredentials(supportedExchanges: Set<KClass<out Exchange<out ExchangeApi<Transactionable, Transactionable>>>>) {
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
        Misc.printExitMessage()
        System.exit(0)
    } catch (e: IllegalStateException) {
        Misc.printExitMessage()
        System.exit(0)
    }
    line.split(" ").filter { it.isNotBlank() }.forEach { args.add(it) }

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
            Misc.printDonate()
        }
        getString(Action.EXIT.title).toUpperCase(), Action.EXIT.name -> {
            Misc.printExitMessage()
            System.exit(0)
        }
        getString(Action.FOLLOW.title).toUpperCase(), Action.FOLLOW.name -> {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(URI(Misc.facebook))
                Desktop.getDesktop().browse(URI(Misc.twitter))
            }
        }
        "DEBUG" -> {
            DEBUG = true
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




