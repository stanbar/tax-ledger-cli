package com.stasbar.taxledger.exceptions

import com.stasbar.taxledger.getString
import com.stasbar.taxledger.translations.Text

class CredentialsException(what: String, apiName: String, length: Int) : Exception(getString(Text.Exceptions.CREDENTIALS).format(what, apiName, length))