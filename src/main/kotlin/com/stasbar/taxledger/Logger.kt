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

package com.stasbar.taxledger

import com.stasbar.taxledger.translations.Text
import org.fusesource.jansi.Ansi.ansi
import org.fusesource.jansi.AnsiConsole

object Logger {

    fun info(info: String?) {
        info?.let { AnsiConsole.out.println(ansi().a("\n").fgBrightCyan().a(0x2714.toChar()).a(getString(Text.Logger.INFO)).reset().a(info).a("\n")) }
    }


    fun err(info: String?) {
        info?.let { AnsiConsole.out.println(ansi().a("\n").fgBrightRed().a(0x2716.toChar()).a(getString(Text.Logger.ERROR)).reset().a(info).a("\n")) }
    }

}