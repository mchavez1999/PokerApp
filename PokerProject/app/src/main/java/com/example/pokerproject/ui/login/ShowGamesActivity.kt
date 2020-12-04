package com.example.pokerproject.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.core.view.setPadding
import com.example.pokerproject.R
import org.w3c.dom.Text


class ShowGamesActivity : AppCompatActivity() {

    var color = intArrayOf(10, 30, 50, 70, 90, 110, 130, 150)
    var colorIdx = 0
    var idxOP = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_view_item)

        /*
        So I guess what I'll do here is build the UI everytime its opened?
        Probably not super efficient, but it's not like there's enough
        games or users that would make it run any slower
         */

        // Retrieve arrayList of active games and reverse it so our most recent game added is at the top
        var gameList = intent.getSerializableExtra("GameList") as ArrayList<Game>
        gameList.reverse()

        // build UI
        buildUI(gameList)
    }

    // build our UI, which is list of games
    private fun buildUI(gameList : ArrayList<Game>) {

        var gameID = 0
        var linearView = findViewById<LinearLayout>(R.id.gameContainer)

        for (game in gameList) {

            // build single game XML view-box
            var gameView = buildGameView(game, gameID)

            // add view
            linearView.addView(gameView)

            // increment gameID
            gameID += 1
        }
    }


    private fun buildGameView(game: Game, ID: Int) : TextView {

        // handling cascading colors, just cause
        when (colorIdx) {
            0 -> {
                idxOP = 1
                colorIdx += idxOP
            }

            color.size - 1 -> {
                idxOP = -1
                colorIdx += idxOP
            }

            else -> {
                colorIdx += idxOP
            }
        }

        // get color for BG and init TextView
        var colorValue = color[colorIdx]
        var gameView = TextView(this)

        // building view //

        // set ID
        gameView.id = ID

        // set height
        gameView.height = 300

        // set bg/text color
        gameView.setBackgroundColor(Color.rgb(colorValue, colorValue, colorValue))
        gameView.setTextColor(Color.WHITE)

        // set text
        gameView.text = "Game ID: \t$ID\n$game"

        // set margin top
        gameView.setPadding(40, 20, 20, 0)

        // setting each onClickListeners for edit/delete
        gameView.setOnClickListener {
            var editIntent = Intent(applicationContext, EditGameActivity::class.java)
                .putExtra("game", game)
                .putExtra("ID", ID)
            startActivity(editIntent)
        }

        return gameView
    }
}