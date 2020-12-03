package com.example.pokerproject.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class ListActivity : AppCompatActivity() {

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
        val user = findViewById<TextView>(R.id.userID)
        val pass = findViewById<TextView>(R.id.passID)
        val date = findViewById<TextView>(R.id.dateValue)
        val blind = findViewById<TextView>(R.id.blindValue)
        val buyin = findViewById<TextView>(R.id.buyinValue)
        val cashout = findViewById<TextView>(R.id.cashoutValue)
        val username = intent.getStringExtra("Username")
        val password = intent.getStringExtra("Password")
        gameText = findViewById<TextView>(R.id.gameList)
        gameText.movementMethod = ScrollingMovementMethod()
        userpass = username+password
        user.text = "Username: " + username
        pass.text = "Password: " + password

        sharedpreferences = getSharedPreferences(LoginActivity.mypreference, Context.MODE_PRIVATE)
        var json = sharedpreferences.getString(userpass, null)
        gameList = json?.let { Gson().fromJson<Game>(it) }!!

        val addGame = findViewById<Button>(R.id.addGame) as Button
        addGame.setOnClickListener{
            if(date.text.toString().trim().isNotEmpty() && blind.text.toString().trim().isNotEmpty() && buyin.text.toString().trim().isNotEmpty() && cashout.text.toString().trim().isNotEmpty()){
                add(
                    date.text.toString(),
                    blind.text.toString().toDouble(),
                    buyin.text.toString().toDouble(),
                    cashout.text.toString().toDouble()
                );
            }else{
                Toast.makeText(this, "Error: Missing values", Toast.LENGTH_SHORT).show()
            }
        }

        val showGames = findViewById<Button>(R.id.showGames) as Button
        showGames.setOnClickListener{
           showGames()
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


}
