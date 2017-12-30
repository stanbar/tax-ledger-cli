package com.stasbar.taxledger.exchanges.bitbay.requests

class HistoryRequest(val currency: String, val limit: String? = null, val offset : String? = null) : BaseRequest() {
    override val method: Method = Method.HISTORY

    override fun toMap(): MutableMap<String, String> {
        val map = super.toMap()
        map.put("currency", currency)
        limit?.let { map.put("limit", it) }
        offset?.let { map.put("offset", it) }
        return map
    }
}