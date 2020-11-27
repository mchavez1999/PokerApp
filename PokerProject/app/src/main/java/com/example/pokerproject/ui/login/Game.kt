package com.example.pokerproject.ui.login

import java.util.*

data class Game(val game_status: status, val gtype: game_type, val startTime: Date, val endTime: Date, val location: String, val buyIn: Int, val cashOut: Int, val blindSize: Int ) {
    enum class status { 
        IN_PROGRESS, COMPLETED, 
    }


    enum class game_type {
        TEXAS, OMAHA, DRAW, SEVCARDSTUD
    }


}
