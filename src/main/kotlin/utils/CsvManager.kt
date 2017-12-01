package utils

import api.Constants.dateFormat
import api.CsvConversionException
import models.History
import translations.Translations
import java.io.*
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

object CsvManager {

    fun saveToFile(histories: List<History>, fileName: String = fileName()) {
        try {
            val file = File(fileName)
            file.parentFile.mkdirs()
            val writer = FileWriter(file)
            CsvManager.writeLine(writer,
                    Arrays.asList("time", "operation_type", "amount", "currency", "comment", "balance_after"))

            val translationCounter: MutableMap<String, BigDecimal> = HashMap(4)


            translationCounter.put("+income", BigDecimal.ZERO)
            translationCounter.put("-pay_for_currency", BigDecimal.ZERO)
            translationCounter.put("-fee", BigDecimal.ZERO)
            translationCounter.put("+currency_transaction", BigDecimal.ZERO)
            translationCounter.put("-withdraw", BigDecimal.ZERO)

            for (history in histories) {
                val operation = history.operationType
                val currentValue = translationCounter[operation]!!
                translationCounter.put(operation, currentValue.add(history.amount))
                CsvManager.writeLine(writer, history.toCsv())
            }
            CsvManager.writeLine(writer, Arrays.asList<String>(""))
            CsvManager.writeLine(writer, Arrays.asList<String>("","Summary"))
            translationCounter.forEach { t, u ->
                CsvManager.writeLine(writer, Arrays.asList<String>("",Translations.translate(t), u.toString()))
            }

            writer.flush()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }


    }

    private fun fileName() = "outputs/history_${SimpleDateFormat("dd_MM_yyyy_hhmmss").format(Date(System.currentTimeMillis()))}.csv"


    private val DEFAULT_SEPARATOR = ','

    @Throws(IOException::class)
    @JvmOverloads
    fun writeLine(w: Writer, values: List<String>, separatorsParam: Char = DEFAULT_SEPARATOR, customQuote: Char = ' ') {
        var separators = separatorsParam

        var first = true

        //default customQuote is empty

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR
        }

        val sb = StringBuilder()
        for (value in values) {
            if (!first) {
                sb.append(separators)
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value))
            } else {
                sb.append(customQuote).append(followCVSformat(value)).append(customQuote)
            }

            first = false
        }
        sb.append("\n")
        w.append(sb.toString())


    }

    //https://tools.ietf.org/html/rfc4180
    private fun followCVSformat(value: String): String {

        var result = value
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"")
        }
        return result

    }

    fun fromCsv(file: File): ArrayList<History> {
        val histories = ArrayList<History>()
        val csvFile = file.absolutePath
        val cvsSplitBy = ","
        try {
            BufferedReader(FileReader(csvFile)).use { br ->
                val lines = br.readLines()
                lines.forEachIndexed { _, line ->
                    if (line.contains("id")) // Is the header row
                        return@forEachIndexed
                    try {
                        val history = createHistoryFromCsv(line.split(cvsSplitBy))
                        histories.add(history)
                    } catch (e: CsvConversionException) {
                        e.printStackTrace()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    } catch (e: IndexOutOfBoundsException) {
                        e.printStackTrace()
                    }

                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return histories
    }

    @Throws(CsvConversionException::class)
    private fun createHistoryFromCsv(csv: List<String>): History {
        if (csv.size != 7)
            throw IllegalArgumentException("Wrong csv, should have length of 7")
        val id = csv[0]
        if (id.isBlank())
            throw CsvConversionException("History id can not be empty", csv)

        val timeString = csv[1]
        if (timeString.isBlank())
            throw CsvConversionException("History time can not be empty", csv)

        val time = try {
            dateFormat.parse(timeString)
        } catch (e: NumberFormatException) {
            throw CsvConversionException("Could not parse time: $timeString", csv)
        }


        val operationType = csv[2]
        if (operationType.isBlank())
            throw CsvConversionException("Operation type can not be empty", csv)

        val amountString = csv[2]
        if (operationType.isBlank())
            throw CsvConversionException("Operation type can not be empty", csv)
        val amount = try {
            BigDecimal(amountString)
        } catch (e: NumberFormatException) {
            throw CsvConversionException("Could not parse amount: $amountString", csv)
        }


        val currency = csv[3]
        if (operationType.isBlank())
            throw CsvConversionException("Currency can not be empty", csv)

        val comment = csv[4]

        val balanceAfterString = csv[5]
        val balanceAfter = try {
            BigDecimal(balanceAfterString)
        } catch (e: NumberFormatException) {
            throw CsvConversionException("Could not parse balanceAfter: $balanceAfterString", csv)
        }

        return History(id, time, operationType, amount, currency, comment, balanceAfter)

    }

    fun printSummaryOf(file: File) {
        val map = HashMap<String, BigDecimal>(5)
        map.put("Otrzymanie środków", BigDecimal.ZERO)
        map.put("Zapłata za zakup waluty", BigDecimal.ZERO)
        map.put("Zakup waluty", BigDecimal.ZERO)
        map.put("Prowizja od transakcji", BigDecimal.ZERO)
        map.put("Wypłata środków na konto", BigDecimal.ZERO)


        val reader = FileReader(file)
        reader.readLines()
                .filterIndexed { index, s -> index != 0 }
                .forEach {
                    val splitted = it.split(",")
                    val operationType = splitted[2]
                    val value = BigDecimal(splitted[3])
                    val currentValue = map.getOrDefault(operationType, BigDecimal.ZERO)
                    map.put(operationType, currentValue + value)
                }


        map.forEach { t, u ->
            println("$t: $u")
        }

    }


}

fun main(args: Array<String>) {
    CsvManager.printSummaryOf(File("/Users/admin1/Downloads/BITBAY_2017.csv"))
}