package com.example.pokerproject.ui.login

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
        return  "Date: \t\t\t\t\t\t\t$date\n" +
                "Duration: \t\t\t\t$dur hours\n" +
                "Location: \t\t\t\t$location\n" +
                "Game Type: \t\t$gameType\n" +
                "Small Blind: \t\t$ $smallBlind\n" +
                "Big Blind: \t\t\t\t$ $bigBlind\n" +
                "Buy-in: \t\t\t\t\t\t$ $buyin\n" +
                "Cashout: \t\t\t\t$ $cashout"
    }
    fun toLocalDate(date :String): LocalDate{
        return LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    }
}

