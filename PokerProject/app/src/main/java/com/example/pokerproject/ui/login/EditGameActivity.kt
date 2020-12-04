package com.example.pokerproject.ui.login

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerproject.R


class EditGameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_view)

        // get game
        var game = intent.getSerializableExtra("game") as Game
        var gameID = intent.getSerializableExtra("ID") as Int

        // get all relevant fields
        val date = findViewById<TextView>(R.id.dateValue)
        val blind = findViewById<TextView>(R.id.blindValue)
        val buyIn = findViewById<TextView>(R.id.buyinValue)
        val cashOut = findViewById<TextView>(R.id.cashoutValue)

        // set relevant fields
        date.text = game.date
        blind.text = game.blind.toString()
        buyIn.text = game.buyin.toString()
        cashOut.text = game.cashout.toString()

        // get header and set it to display current game
        val header = findViewById<TextView>(R.id.buffer)
        header.text = "Editing\nGame ID: $gameID"

        // get buttons
        val submitBtn = findViewById<Button>(R.id.submit)
        val deleteBtn = findViewById<Button>(R.id.delete)
    }
}