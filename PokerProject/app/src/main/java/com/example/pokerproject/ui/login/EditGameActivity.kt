package com.example.pokerproject.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.Delegates


class EditGameActivity : AppCompatActivity() {

    lateinit var sharedpreferences: SharedPreferences
    lateinit var userpass: String
    private lateinit var username : String
    private lateinit var password : String
    lateinit var gameList: ArrayList<Game>
    var gameID by Delegates.notNull<Int>()
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<ArrayList<Game>>(
        json,
        object : TypeToken<ArrayList<Game>>() {}.type
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_view)

        // get username, password, and userpass for sharedPref
        userpass = intent.getStringExtra("userpass") as String
        username = intent.getStringExtra("username") as String
        password = intent.getStringExtra("password") as String

        // get JSON
        sharedpreferences = getSharedPreferences(LoginActivity.mypreference, Context.MODE_PRIVATE)
        var json = sharedpreferences.getString(userpass, null)
        gameList = json?.let { Gson().fromJson<Game>(it) }!!

        // get game
        var game = intent.getSerializableExtra("game") as Game
        gameID = intent.getSerializableExtra("ID") as Int

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

        // submit edits
        submitBtn.setOnClickListener {
            if(date.text.toString().trim().isNotEmpty() && blind.text.toString().trim().isNotEmpty() && buyIn.text.toString().trim().isNotEmpty() && cashOut.text.toString().trim().isNotEmpty()){
                submit(
                    date.text.toString(),
                    blind.text.toString().toDouble(),
                    buyIn.text.toString().toDouble(),
                    cashOut.text.toString().toDouble(),
                );

                // success game added toast
                Toast.makeText(this, "Successful Edits to Game ID: $gameID", Toast.LENGTH_SHORT).show()
            }
        }

        // delete game
        deleteBtn.setOnClickListener {
            delete()
        }
    }

    private fun submit(date: String, blind: Double, buyin: Double, cashout: Double) {

        // search each game
        for (game in gameList) {

            // if we find our game we want to edit
            if (game.ID == gameID) {

                // set fields
                game.date = date
                game.blind = blind
                game.buyin = buyin
                game.cashout = cashout

                // save in sharedPref
                save()

                // start ShowGamesActivity
                val intent = Intent(applicationContext, ShowGamesActivity::class.java)
                    .putExtra("GameList", gameList)
                    .putExtra("username", username)
                    .putExtra("password", password)
                    .putExtra("userpass", userpass)
                startActivity(intent)
            }
        }
    }

    /* deletes a game */
    private fun delete() {

        /* use iterator() to avoid concurrent modification */
        val gameListIter = gameList.iterator()
        while (gameListIter.hasNext()) {
            val game = gameListIter.next()

            // if we find our game we want to delete
            if (game.ID == gameID) {

                // delete it and save() our deletion
                gameListIter.remove()
                save()

                // upon deletion, either return to ShowGameActivity or CreateGameActivity (if no games are left)
                if (gameList.isEmpty()) {
                    Toast.makeText(this, "No Games Left.\nAdd Game(s).", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CreateGameActivity::class.java).putExtra("Username", username).putExtra("Password", password)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Deleted Game ID: $gameID", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, ShowGamesActivity::class.java)
                        .putExtra("GameList", gameList)
                        .putExtra("username", username)
                        .putExtra("password", password)
                        .putExtra("userpass", userpass)
                    startActivity(intent)
                }
            }
        }
    }

    // should make updated to sharedpreferences
    private fun save(){
        val editor = sharedpreferences.edit()
        var gson = Gson()
        var json = gson.toJson(gameList)
        editor.putString(userpass, json)
        // editor.apply();
        editor.commit()
    }
}