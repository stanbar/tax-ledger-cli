package requests

import com.google.gson.annotations.SerializedName

abstract class BaseRequest{
    abstract val method : Method
    val moment : Long = System.currentTimeMillis() / 1000

    open fun toMap(): MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map.put("method",method.methodName)
        map.put("moment",moment.toString())
        return map
    }

}
enum class Method(val methodName: String){
    @SerializedName("info")INFO("info"),
    @SerializedName("history")HISTORY("history"),
    @SerializedName("transactions")TRANSACTIONS("transactions");

    override fun toString(): String {
        return methodName
    }


}