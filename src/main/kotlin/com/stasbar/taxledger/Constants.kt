package com.stasbar.taxledger

import java.text.SimpleDateFormat
import java.util.*

object Constants{
    const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"
    val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
}