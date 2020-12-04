package com.example.pokerproject.ui.login

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

class Game(dateOfGame: String, blindVal: Double, buyinVal: Double, cashoutVal: Double) : Serializable {
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
        return "Date: \t\t\t\t$date\nBlind: \t\t\t\t$blind\nBuyin: \t\t\t\t$buyin\nCashout: \t$cashout"
    }
}

