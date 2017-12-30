package com.stasbar.taxledger

import com.stasbar.taxledger.translations.Text
import org.fusesource.jansi.Ansi.ansi

object Logger {

    fun info(info: String?) {
        info?.let { println(ansi().a("\n").fgBrightCyan().a(0x2714.toChar()).a(getString(Text.Logger.INFO)).reset().a(info).a("\n")) }
    }


    fun err(info: String?) {
        info?.let { println(ansi().a("\n").fgBrightRed().a(0x2716.toChar()).a(getString(Text.Logger.ERROR)).reset().a(info).a("\n")) }
    }

}