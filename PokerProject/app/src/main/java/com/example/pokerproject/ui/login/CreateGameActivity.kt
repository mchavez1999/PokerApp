package com.example.pokerproject.ui.login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class CreateGameActivity : AppCompatActivity() {

    lateinit var sharedpreferences: SharedPreferences
    lateinit var userpass: String
    lateinit var gameList: ArrayList<Game>
    lateinit var gameText: TextView
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<ArrayList<Game>>(
        json,
        object : TypeToken<ArrayList<Game>>() {}.type
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // game parameter inputs
        val date = findViewById<TextView>(R.id.dateValue)
        val blind = findViewById<TextView>(R.id.blindValue)
        val buyin = findViewById<TextView>(R.id.buyinValue)
        val cashout = findViewById<TextView>(R.id.cashoutValue)

        // username and password
        val username = intent.getStringExtra("Username")
        val password = intent.getStringExtra("Password")
        userpass = username+password

        // get and build JSON
        sharedpreferences = getSharedPreferences(LoginActivity.mypreference, Context.MODE_PRIVATE)
        var json = sharedpreferences.getString(userpass, null)
        gameList = json?.let { Gson().fromJson<Game>(it) }!!

        // addGame button functionality
        val addGame = findViewById<Button>(R.id.addGame) as Button
        addGame.setOnClickListener{

            // if date is in a valid format, add the game!
            if (!dateValidator(date.text.toString())) {
                Toast.makeText(this, "Invalid Date Format.\nEx: 12/07/2020", Toast.LENGTH_LONG).show()
            } else if(date.text.toString().trim().isNotEmpty() && blind.text.toString().trim().isNotEmpty() && buyin.text.toString().trim().isNotEmpty() && cashout.text.toString().trim().isNotEmpty()){
                add(
                    date.text.toString(),
                    blind.text.toString().toDouble(),
                    buyin.text.toString().toDouble(),
                    cashout.text.toString().toDouble(),
                );

                // success game added toast
                Toast.makeText(this, "Game Successfully Added.", Toast.LENGTH_SHORT).show()
            } else{
                Toast.makeText(this, "Error: Missing values", Toast.LENGTH_SHORT).show()
            }
        }

        // show games button functionality
        val showGames = findViewById<Button>(R.id.showGames) as Button
        showGames.setOnClickListener{

            // see if there are any games to show, If so, start activity
            if (gameList.isNotEmpty()) {
                val intent = Intent(applicationContext, ShowGamesActivity::class.java)
                    .putExtra("GameList", gameList)
                    .putExtra("username", username)
                    .putExtra("password", password)
                    .putExtra("userpass", userpass)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Games to Show. Add Game(s).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun add(date: String, blind: Double, buyin: Double, cashout: Double){
        gameList.add(Game(date, blind, buyin, cashout, gameList.size))
        save()
    }

    private fun save(){
        val editor = sharedpreferences.edit()
        var gson = Gson()
        var json = gson.toJson(gameList)
        editor.putString(userpass, json)
        editor.apply();
    }

    // Ex: 12/07/2020
    private fun dateValidator(date: String) : Boolean {
        val dateRegex = Regex("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d\$")
        return dateRegex.matches(date)
    }


}
