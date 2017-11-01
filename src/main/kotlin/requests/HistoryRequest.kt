package requests

class HistoryRequest(val currency: String, val limit: String?) : BaseRequest() {
    override val method: Method = Method.HISTORY

    override fun toMap(): MutableMap<String, String> {
        val map = super.toMap()
        map.put("currency", currency)
        limit?.let { map.put("limit", it) }
        return map
    }
}