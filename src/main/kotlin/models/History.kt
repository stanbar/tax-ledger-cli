package models

import api.Constants.dateFormat
import com.google.gson.annotations.SerializedName
import translations.Translations
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
        return Arrays.asList<String>(dateFormat.format(time), Translations.translate(operationType), amount.toString(), currency, comment, balanceAfter.toString())
    }

}

