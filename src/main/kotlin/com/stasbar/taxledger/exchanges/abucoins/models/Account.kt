package com.stasbar.taxledger.exchanges.abucoins.models

import java.math.BigDecimal

data class Account(private val id : String, //account id
              private val currency : String, //the currency of the account
              private val balance : BigDecimal, //the founds in the account
              private val available : BigDecimal, //founds available to withdraw or trade
              private val available_btc : BigDecimal, // founds available to withdraw or trade for BTC
              private val hold : BigDecimal, //funds on hold (not available for use)
              private val profile_id : Int //profile id
              )