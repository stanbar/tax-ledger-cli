import api.PrivateApi
import requests.HistoryRequest
import requests.TransactionsRequest
import utils.CsvConverter

var publicKey : String = ""
var privateKey  : String = ""

fun main(args: Array<String>) {
    publicKey = args[0]
    privateKey = args[1]

    history()
    transactions()

}



fun history() {

    val request = HistoryRequest("PLN", null)
    val historyRequest = PrivateApi.getInstance(publicKey, privateKey)
            .service.value.history(request.toMap())

    val histories = historyRequest.execute().body()

    histories?.let {
        it.forEach {
            println(it.toString())
        }
        CsvConverter.saveToFile(it)
    }
}

fun transactions() {
    val request = TransactionsRequest()
    val transactionRequest = PrivateApi.getInstance(publicKey, privateKey)
            .service.value.transactions(request.toMap())

    val transactions = transactionRequest.execute().body()

    transactions?.let {
        it.forEach {
            println(it.toString())
        }
    }

}