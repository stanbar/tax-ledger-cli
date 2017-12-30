package com.stasbar.taxledger

import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi

object Misc {
    val intro =
            "         __             __              \n" +
                    "   _____/ /_____ ______/ /_  ____ ______\n" +
                    "  / ___/ __/ __ `/ ___/ __ \\/ __ `/ ___/\n" +
                    " (__  ) /_/ /_/ (__  ) /_/ / /_/ / /    \n" +
                    "/____/\\__/\\__,_/____/_.___/\\__,_/_/     \n" +
                    "                                        \n"
    val color1 = Ansi.Color.BLUE
    val color2 = Ansi.Color.BLUE
    val contact = ansi()
            .fg(color1).a("         __             ").fgBright(color2).a("__              \n")
            .fg(color1).a("   _____/ /_____ ______").fgBright(color2).a("/ /_  ____ ______\n")
            .fg(color1).a("  / ___/ __/ __ `/ ___/").fgBright(color2).a(" __ \\/ __ `/ ___/\n")
            .fg(color1).a(" (__  ) /_/ /_/ (__  )").fgBright(color2).a(" /_/ / /_/ / /    \n")
            .fg(color1).a("/____/\\__/\\__,_/____/").fgBright(color2).a("_.___/\\__,_/_/     \n")
            .fgBright(color1).a("           taxledger").fg(color1).a("@stasbar.com                \n").reset().toString()


    val taxledger = ansi().fg(color1).a("                                                                              \n")
            .fg(color1).a("████████╗ █████╗ ██╗  ██╗  ").fgBright(color2).a("  ██╗     ███████╗██████╗  ██████╗ ███████╗██████╗ \n")
            .fg(color1).a("╚══██╔══╝██╔══██╗╚██╗██╔╝  ").fgBright(color2).a("  ██║     ██╔════╝██╔══██╗██╔════╝ ██╔════╝██╔══██╗\n")
            .fg(color1).a("   ██║   ███████║ ╚███╔╝   ").fgBright(color2).a("  ██║     █████╗  ██║  ██║██║  ███╗█████╗  ██████╔╝\n")
            .fg(color1).a("   ██║   ██╔══██║ ██╔██╗   ").fgBright(color2).a("  ██║     ██╔══╝  ██║  ██║██║   ██║██╔══╝  ██╔══██╗\n")
            .fg(color1).a("   ██║   ██║  ██║██╔╝ ██╗  ").fgBright(color2).a("  ███████╗███████╗██████╔╝╚██████╔╝███████╗██║  ██║\n")
            .fg(color1).a("   ╚═╝   ╚═╝  ╚═╝╚═╝  ╚═╝  ").fgBright(color2).a("  ╚══════╝╚══════╝╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝\n")
            .fg(color1).a("                                                                              \n").reset().toString()

    val gitlink = "https://taxledger.stasbar.com"
}