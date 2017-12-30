package com.stasbar.taxledger

import com.stasbar.taxledger.models.Transaction


interface ExchangeApi{
    fun transactions(): List<Transaction>
}



