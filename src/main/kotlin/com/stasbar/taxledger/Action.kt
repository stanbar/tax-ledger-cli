package com.stasbar.taxledger

import com.stasbar.taxledger.translations.Text.Actions.*


enum class Action(val title: String, val symbol: Int, val description: String) {
    TRANSACTIONS(TRANSACTIONS_TITLE, 0x1F4D6, TRANSACTIONS_DESC),
    EXCHANGES(EXCHANGES_TITLE, 0x1F511, EXCHANGES_DESC),
    CONTACT(CONTACT_TITLE, 0x1F4E7, CONTACT_DESC),
    DONATE(DONATE_TITLE, 0x1F44D, DONATE_DESC),
    EXIT(EXIT_TITLE, 0x1F6AA, EXIT_DESC);

}
