package com.example.pokerproject.ui.login

import java.util.*

class Game(dateOfGame: String, blindVal: Double, buyinVal: Double, cashoutVal: Double) {
    var date: String = dateOfGame
    var blind = 0.0
    var buyin = 0.0
    var cashout = 0.0

    init{
        blind = blindVal
        buyin = buyinVal
        cashout = cashoutVal
    }

    override fun toString(): String {
        return "Date: $date\nBlind: $blind\nBuyin$buyin\nCashout: $cashout"
    }
}

