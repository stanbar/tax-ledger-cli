/*
 * Copyright (c) 2018 Stanislaw stasbar Baranski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *          __             __
 *    _____/ /_____ ______/ /_  ____ ______
 *   / ___/ __/ __ `/ ___/ __ \/ __ `/ ___/
 *  (__  ) /_/ /_/ (__  ) /_/ / /_/ / /
 * /____/\__/\__,_/____/_.___/\__,_/_/
 *            taxledger@stasbar.com
 */

package com.stasbar.taxledger.exchanges.bitmarket.requests

import com.google.gson.annotations.SerializedName

abstract class BitmarketBaseRequest {
    abstract val method : Method

    open fun toMap(): MutableMap<String, String> {
        val map: MutableMap<String, String> = HashMap()
        map.put("method",method.methodName)
        map.put("tonce", (System.currentTimeMillis() / 1000).toString())
        return map
    }

}
enum class Method(val methodName: String){
    @SerializedName("trades")TRADES("trades"),
    @SerializedName("info")
    INFO("info"),
    @SerializedName("history")
    HISTORY("history");

    override fun toString(): String {
        return methodName
    }


}