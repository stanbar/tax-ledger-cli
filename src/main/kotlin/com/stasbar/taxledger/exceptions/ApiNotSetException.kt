package com.stasbar.taxledger.exceptions

import com.stasbar.taxledger.getString
import com.stasbar.taxledger.translations.Text

class ApiNotSetException(exchangeName: String) : Exception(getString(Text.Exceptions.API_NOT_SET).format(exchangeName))