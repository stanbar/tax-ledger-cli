package com.stasbar.taxledger.exchanges.abucoins.models

import java.math.BigDecimal

data class PaymentMethod(
        val id : String,
        val type : String,
        val name : String,
        val currency : String,
        val allow_buy : Boolean,
        val allow_sell : Boolean,
        val allow_deposit : Boolean,
        val allow_withdraw : Boolean,
        val limits : List<Limit>
)

data class Limit(
        val buy : BigDecimal,
        val sell : BigDecimal,
        val deposit : BigDecimal,
        val withdraw : BigDecimal)