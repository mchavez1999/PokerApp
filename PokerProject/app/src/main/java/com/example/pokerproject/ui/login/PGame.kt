package com.example.pokerproject.ui.login

import java.time.LocalDate
import java.time.format.DateTimeFormatter
class PGame(copy: Game) {
    var date: LocalDate = toLocalDate(copy.date)
    var gameType: String = copy.gameType
    var dur = copy.dur
    var bigBlind = copy.bigBlind
    var buyin = copy.buyin
    var cashout =copy.cashout

    private fun toLocalDate(date :String): LocalDate {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    }
    }
