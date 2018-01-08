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

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import java.util.*

object Misc {
    val color1 = Ansi.Color.YELLOW
    val color2 = Ansi.Color.BLUE
    val contact = ansi().a("\n")
            .fg(color1).a("         __             ").fgBright(color2).a("__              \n")
            .fg(color1).a("   _____/ /_____ ______").fgBright(color2).a("/ /_  ____ ______\n")
            .fg(color1).a("  / ___/ __/ __ `/ ___/").fgBright(color2).a(" __ \\/ __ `/ ___/\n")
            .fg(color1).a(" (__  ) /_/ /_/ (__  )").fgBright(color2).a(" /_/ / /_/ / /    \n")
            .fg(color1).a("/____/\\__/\\__,_/____/").fgBright(color2).a("_.___/\\__,_/_/     \n")
            .fgBright(color2).a("           taxledger").fg(color1).a("@stasbar.com                \n").reset().toString()


    val taxledger = ansi().fg(color1).a("                                                                              \n")
            .fg(color1).a("████████╗ █████╗ ██╗  ██╗  ").fgBright(color2).a("  ██╗     ███████╗██████╗  ██████╗ ███████╗██████╗ \n")
            .fg(color1).a("╚══██╔══╝██╔══██╗╚██╗██╔╝  ").fgBright(color2).a("  ██║     ██╔════╝██╔══██╗██╔════╝ ██╔════╝██╔══██╗\n")
            .fg(color1).a("   ██║   ███████║ ╚███╔╝   ").fgBright(color2).a("  ██║     █████╗  ██║  ██║██║  ███╗█████╗  ██████╔╝\n")
            .fg(color1).a("   ██║   ██╔══██║ ██╔██╗   ").fgBright(color2).a("  ██║     ██╔══╝  ██║  ██║██║   ██║██╔══╝  ██╔══██╗\n")
            .fg(color1).a("   ██║   ██║  ██║██╔╝ ██╗  ").fgBright(color2).a("  ███████╗███████╗██████╔╝╚██████╔╝███████╗██║  ██║\n")
            .fg(color1).a("   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝  ").fgBright(color2).a("  ╚══════╝╚══════╝╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝\n")
            .fg(color1).a("                                                                              \n").reset().toString()

    val gitlink = "https://taxledger.stasbar.com"

    val donateMap: LinkedHashMap<String, Pair<Ansi.Color, String>> by lazy {
        val map = LinkedHashMap<String, Pair<Ansi.Color, String>>()
        map.put("Bitcoin", Pair(Ansi.Color.YELLOW, "3QgRku1UkFbyVyBbdLFxfxKmgFwDvT5feP"))
        map.put("Etherium", Pair(Ansi.Color.BLUE, "0x03Ba2f3907fcA09867C7A1F4f218D7B5eA052997"))
        map.put("Bitcoin Cash", Pair(Ansi.Color.YELLOW, "1LTfvN44Lmf6eqHn9B1bAnJV4rbLQKpduK"))
        map.put("Litecoin", Pair(Ansi.Color.WHITE, "M88GxayRm4YQ78KuLJ9tYuAYESrd9uPEAg"))
        map.put("Dash", Pair(Ansi.Color.BLUE, "XmnmWK2iN73X9qqcE4NMX4VTVVC2YjrCMS"))
        map.put("NEO", Pair(Ansi.Color.GREEN, "ANSHr2MMLM4YDFCpriRiE3WjnxMYcXteX3"))
        map.put("Lisk", Pair(Ansi.Color.BLUE, "7610839096466012755L"))
        map.put("IOTA", Pair(Ansi.Color.WHITE, "GINAXBSANTACUZMDBYXSWVGKFCMJTUQPSJLYS9K9AAP9S9QCGPJAKCCZHSHV9RXWUMNMGMZNPFLHRLWHCFYTBDEJ99"))
        map
    }




}