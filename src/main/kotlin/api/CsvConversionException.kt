package api

import java.util.*

class CsvConversionException(message : String, val csv : List<String>) : RuntimeException(message){
    override val message: String?
        get() = Arrays.toString(csv.toTypedArray())
}