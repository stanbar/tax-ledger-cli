package requests

class TransactionsRequest(val market: String? = null) : BaseRequest() {
    override val method: Method = Method.TRANSACTIONS

    override fun toMap(): MutableMap<String, String> {
        val map = super.toMap()
        market?.let { map.put("market", it) }
        return map
    }
}