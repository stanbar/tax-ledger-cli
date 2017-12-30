package com.stasbar.taxledger.exceptions

import com.stasbar.taxledger.getString
import com.stasbar.taxledger.translations.Text

class TooManyCredentialsException(exchangeName: String) : Exception(getString(Text.Exceptions.TOO_MANY_ARGS) + exchangeName)