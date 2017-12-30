package com.stasbar.taxledger.exchanges.bitbay.requests

class TransactionsRequest(val market: String? = null, val limit : String? = null, val offset : String? = null) : BaseRequest() {
    override val method: Method = Method.TRANSACTIONS

    override fun toMap(): MutableMap<String, String> {
        val map = super.toMap()
        market?.let { map.put("market", it) }
        limit?.let { map.put("limit", it) }
        offset?.let { map.put("offset", it) }
        return map
    }
}