package com.stasbar.taxledger.exchanges.bitmarket.requests

import com.google.gson.annotations.SerializedName

abstract class BaseRequest{
    abstract val method : Method
    val tonce : Long = System.currentTimeMillis() / 1000

    open fun toMap(): MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map.put("method",method.methodName)
        map.put("tonce",tonce.toString())
        return map
    }

}
enum class Method(val methodName: String){
    @SerializedName("trades")TRADES("trades"),
    @SerializedName("info")INFO("info");

    override fun toString(): String {
        return methodName
    }


}