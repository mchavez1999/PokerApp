package com.example.pokerproject.ui.login

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.util.*

class Game(dateOfGame: String, location: String, duration: Double, gameType: String, smallBlindVal: Double, bigBlindVal: Double, buyinVal: Double, cashoutVal: Double, idVal: Int) : Serializable {
    var date: String = dateOfGame
    var location: String = location
    var gameType: String = gameType
    var dur = 0.0
    var smallBlind = 0.0
    var bigBlind = 0.0
    var buyin = 0.0
    var cashout = 0.0
    var ID = -1

    init{
        dur = duration
        smallBlind = smallBlindVal
        bigBlind = bigBlindVal
        buyin = buyinVal
        cashout = cashoutVal
        ID = idVal
    }

    override fun toString(): String {
        return "Date: \t\t\t\t\t\t\t$date    DURATION: $dur\nLocation: \t\t\t\t$location    Game Type: \t\t$gameType\nSmall Blind: \t\t$smallBlind    Big Blind: \t\t\t\t$bigBlind\nBuy-in: \t\t\t\t\t\t$buyin    Cashout: \t\t\t\t$cashout"
    }
}

