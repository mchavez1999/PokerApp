package com.example.pokerproject.ui.login

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

class Game(dateOfGame: String, blindVal: Double, buyinVal: Double, cashoutVal: Double, idVal: Int) : Serializable {
    var date: String = dateOfGame
    var blind = 0.0
    var buyin = 0.0
    var cashout = 0.0
    var ID = -1

    init{
        blind = blindVal
        buyin = buyinVal
        cashout = cashoutVal
        ID = idVal
    }

    override fun toString(): String {
        return "Date: \t\t\t\t$date\nBlind: \t\t\t\t$blind\nBuyin: \t\t\t\t$buyin\nCashout: \t$cashout"
    }
}

