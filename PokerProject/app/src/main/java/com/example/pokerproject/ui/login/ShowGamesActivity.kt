package com.example.pokerproject.ui.login

import android.app.ActionBar
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Layout
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import com.example.pokerproject.R


class ShowGamesActivity : AppCompatActivity() {

    var color = intArrayOf(10, 30)
    var colorIdx = 1
    private lateinit var userpass : String
    private lateinit var username : String
    private lateinit var password : String
    private lateinit var gameList : ArrayList<Game>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_view_item)

        /*
        So I guess what I'll do here is build the UI every time its called?
        Probably not super efficient, might be worth changing later on..
         */

        // get username, password, and userpass for sharedPref
        userpass = intent.getStringExtra("userpass") as String
        username = intent.getStringExtra("username") as String
        password = intent.getStringExtra("password") as String

        // get ADD GAME button, if clicked go to CreateGameActivity
        val addGame = findViewById<Button>(R.id.addGame)
        addGame.setOnClickListener {
            val intent = Intent(this, CreateGameActivity::class.java)
                .putExtra("Username", username)
                .putExtra("Password", password)
            startActivity(intent)
            finish()
        }

        // Retrieve arrayList of active games and reverse it so our most recent game added is at the top
        gameList = intent.getSerializableExtra("GameList") as ArrayList<Game>
        gameList.reverse()

        // build UI
        buildUI(gameList)
    }

    // build our UI, which is list of games
    private fun buildUI(gameList: ArrayList<Game>) {

        // get linear layout view
        var linearView = findViewById<LinearLayout>(R.id.gameContainer)

        // build game view-box for each game
        for (game in gameList) {

            // build single game XML view-box
            var gameView = buildGameView(game, game.ID)

            // add view
            linearView.addView(gameView)
        }
    }


    // build view for a single game
    private fun buildGameView(game: Game, ID: Int) : TextView {

        // handling cascading colors, just cause
        colorIdx = when (colorIdx) {
            0 -> {
                1
            }
            else -> {
                0
            }
        }

        // get color for BG and init TextView
        var colorValue = color[colorIdx]
        var gameView = TextView(this)

        // building view //

        // set ID, although this seems pointless
        gameView.id = ID

        // set height
        gameView.height = 450 // 275

        // set bg/text color
        gameView.setBackgroundColor(Color.rgb(colorValue, colorValue, colorValue))
        gameView.setTextColor(Color.WHITE)

        // set text
        gameView.text = "Game ID: \t\t\t\t$ID\n$game"

        // set padding top and bottom
        gameView.setPadding(40, 10, 70, 10) // top 10

        // set margins
        var marginParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        marginParams.setMargins(0,10,0,0)
        gameView.layoutParams = marginParams

        // add in edit-pencil
        gameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.edit_pencil_sm, 0)

        // setting each onClickListeners for edit/delete
        gameView.setOnClickListener {
            var editIntent = Intent(applicationContext, EditGameActivity::class.java)
                .putExtra("game", game)
                .putExtra("ID", ID)
                .putExtra("username", username)
                .putExtra("password", password)
                .putExtra("userpass", userpass)
                .putExtra("gameList", gameList)
            startActivity(editIntent)
            finish()
        }

        return gameView
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Closing Activity")
            .setMessage("Are you sure you want to exit the app?")
            .setPositiveButton("Yes",
                DialogInterface.OnClickListener { dialog, which -> finish() })
            .setNegativeButton("No", null)
            .show()
    }
}