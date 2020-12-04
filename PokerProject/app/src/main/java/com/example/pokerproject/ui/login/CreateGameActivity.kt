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
                    cashout.text.toString().toDouble()
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
                val intent = Intent(applicationContext, ShowGamesActivity::class.java).putExtra("GameList", gameList)
                startActivity(intent)
            } else {
                Toast.makeText(this, "No Games to Show. Add Game(s).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun add(date: String, blind: Double, buyin: Double, cashout: Double){
        gameList.add(Game(date, blind, buyin, cashout))
        save()
    }

    private fun showGames(){
        var text = ""
        var count = 1
        for(g in gameList){
            text += "Game: $count\n$g\n\n"
            count++
        }
        if(text.isEmpty()){
            text = "Games: None"
            Toast.makeText(this, "No games to show", Toast.LENGTH_SHORT).show()
        }
        gameText.text = text
    }

    private fun save(){
        val editor = sharedpreferences.edit()
        var gson = Gson()
        var json = gson.toJson(gameList)
        editor.putString(userpass, json)
        editor.apply();
    }

    // accepts a bunch of different date formats:
    // Ex: 12/07/2020 | 12/07/20 | 12.07.2020 | 12.07.20 | 12-07-2020 | 12-07-20
    private fun dateValidator(date: String) : Boolean {
        val dateRegex = Regex("^(?:(?:31(\\/|-|\\.)" +
                "(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)" +
                "(?:0?[13-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})" +
                "\$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?" +
                "(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))\$|" +
                "^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4" +
                "(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$")
        return dateRegex.matches(date)
    }


}
