package com.stasbar.taxledger

import java.io.File
import java.io.FileReader
import java.io.PrintWriter
import kotlin.reflect.KClass

class PreferencesManager(val file: File = File("credentials.txt")) {


    fun save(supportedExchanges: Set<KClass<out Exchange<ExchangeApi>>>) {
        val writer = PrintWriter(file)
        supportedExchanges.map { it.objectInstance!! }.filter{ it.isSet() }.forEach { it.printCredentials(writer) }
    }

    fun load(): List<String> {
        if (!file.exists()) file.createNewFile()
        return FileReader(file).readLines()
    }

}