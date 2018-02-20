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

import com.stasbar.taxledger.models.Transactionable
import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import kotlin.reflect.KClass

object PreferencesManager {
    val workingDir: File by lazy {
        if (System.getProperty("user.dir").contains(".app/")) {
            val taxLedgerFolder = File(System.getProperty("user.home"), "TaxLedger")
            if (!taxLedgerFolder.exists()) taxLedgerFolder.mkdir()
            taxLedgerFolder
        } else
            File(System.getProperty("user.dir"))
    }

    val transactionsDir: File by lazy {
        val dir = File(workingDir, "transactions")
        if (!dir.exists()) dir.mkdir()
        dir

    }
    val credentials: File = File(workingDir, "credentials.txt")

    fun save(supportedExchanges: Set<KClass<out Exchange<out ExchangeApi<Transactionable, Transactionable>>>>) {
        val writer = PrintWriter(credentials)
        supportedExchanges.map { it.objectInstance!! }.filter { it.isSet() }.forEach { it.printCredentials(writer) }
    }

    fun load(): List<String> {
        if (!credentials.exists()) credentials.createNewFile()
        return FileReader(credentials).readLines()
    }

}