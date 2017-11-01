package models

import api.Constants
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.*

data class Transaction(var id: String = "",
                       var date: Date = Date(),
                       @SerializedName("type") var operationType: String = "",
                       var amount: BigDecimal = BigDecimal.ZERO,
                       var market: String = "",
                       var rate: String = "",
                       var price: BigDecimal = BigDecimal.ZERO) {
    fun toCsv(): List<String> {
        return Arrays.asList<String>(id, Constants.dateFormat.format(date), operationType, amount.toString(), market, rate, price.toString())
    }
}