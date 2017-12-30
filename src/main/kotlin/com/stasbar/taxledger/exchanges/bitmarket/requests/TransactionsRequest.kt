package com.stasbar.taxledger.exchanges.bitmarket.requests

class TransactionsRequest(val market: String? = null, val count : String? = null, val start : String? = null) : BaseRequest() {
    override val method: Method = Method.TRADES

    override fun toMap(): MutableMap<String, String> {
        val map = super.toMap()
        market?.let { map.put("market", it) }
        count?.let { map.put("count", it) }
        start?.let { map.put("start", it) }
        return map
    }
}