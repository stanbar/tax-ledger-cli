package models

import api.Constants.dateFormat
import com.google.gson.annotations.SerializedName
import java.math.BigDecimal
import java.util.*

data class History(var id: String = "",
                   var time: Date = Date(),
                   @SerializedName("operation_type") var operationType: String = "",
                   var amount: BigDecimal = BigDecimal.ZERO,
                   var currency: String = "",
                   var comment: String = "",
                   @SerializedName("balance_after") var balanceAfter: BigDecimal = BigDecimal.ZERO

) {
    fun toCsv(): List<String> {
        return Arrays.asList<String>(id, dateFormat.format(time), operationType, amount.toString(), currency, comment, balanceAfter.toString())
    }

}

