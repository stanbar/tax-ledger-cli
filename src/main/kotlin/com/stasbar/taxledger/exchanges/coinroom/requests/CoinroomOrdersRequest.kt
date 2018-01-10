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

package com.stasbar.taxledger.exchanges.coinroom.requests

import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class CoinroomOrdersRequest(
        val limit: Int? = null,
        val page: Int? = null,
        val realCurrency: String? = null,
        val cryptoCurrency: String? = null,
        val status: String? = null,
        val type: String? = null) {


    val ALGORITHM: String = "HmacSHA256"
    private fun hash256(privateKey: String): String {
        val stringBuilder = StringBuilder()
        limit?.let { stringBuilder.append(it) }
        page?.let { stringBuilder.append(it) }
        realCurrency?.let { stringBuilder.append(it) }
        cryptoCurrency?.let { stringBuilder.append(it) }
        status?.let { stringBuilder.append(it) }
        type?.let { stringBuilder.append(it) }

        val mac = Mac.getInstance(ALGORITHM)
        val secret = SecretKeySpec(Base64.getDecoder().decode(privateKey), ALGORITHM)
        mac.init(secret)
        return Base64.getEncoder().encodeToString(mac.doFinal(stringBuilder.toString().toByteArray()))
    }

    fun toMap(privateKey: String): MutableMap<String, String> {
        val map = HashMap<String, String>()
        limit?.let { map.put("limit", it.toString()) }
        page?.let { map.put("page", it.toString()) }
        realCurrency?.let { map.put("realCurrency", it) }
        cryptoCurrency?.let { map.put("cryptoCurrency", it) }
        status?.let { map.put("status", it) }
        type?.let { map.put("type", it) }
        map.put("sign", hash256(privateKey))
        return map
    }
}

