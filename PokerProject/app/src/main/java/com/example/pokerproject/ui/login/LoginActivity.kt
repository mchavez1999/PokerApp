package com.example.pokerproject.ui.login

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast

import com.example.pokerproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LoginActivity : AppCompatActivity() {
    lateinit var sharedpreferences: SharedPreferences
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<ArrayList<Game>>(
        json,
        object : TypeToken<ArrayList<Game>>() {}.type
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedpreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE)
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)

        val login = findViewById<Button>(R.id.login) as Button
        login.setOnClickListener{
            Login(username.text.toString(), password.text.toString());
        }

        val register = findViewById<Button>(R.id.register) as Button
        register.setOnClickListener{
            Register(username.text.toString(), password.text.toString());
        }

       /* val clear = findViewById<Button>(R.id.clear) as Button
        clear.setOnClickListener(){
            clearData(username.text.toString(), password.text.toString())
        }

        */
    }

    private fun Register(username: String, password: String) {
        var gameList = ArrayList<Game>()
        val userpass = username+password
        var gson = Gson()
        var json = gson.toJson(gameList)
        val editor = sharedpreferences.edit()

        // check for valid passwords
        if (!validPassword(password)) {
            Toast.makeText(this, "Invalid Password.\nRequirements: Minimum of 6 Characters, 1 Letter, and 1 Number", Toast.LENGTH_LONG).show()
            return
        }

        if(sharedpreferences.contains(username)){
            Toast.makeText(this, "Error: User already exists", Toast.LENGTH_SHORT).show()
        }else{
            editor.putString(username, password)
            editor.putString(userpass, json)
            editor.apply();
            val intent = Intent(this, CreateGameActivity::class.java).putExtra("Username", username).putExtra("Password", password)
            startActivity(intent)
        }
    }

    private fun Login(username: String, password: String) {
        if(!sharedpreferences.contains(username)){
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show()
        } else if(!sharedpreferences.getString(username, null).equals(password)) {
            Toast.makeText(this, "Error: Incorrect password", Toast.LENGTH_SHORT).show()
        } else{

            var userpass = username+password
            var json = sharedpreferences.getString(userpass, null)
            var gameList = json?.let { Gson().fromJson<Game>(it) }!!

            // Welcome Message
            Toast.makeText(this, "Welcome $username!", Toast.LENGTH_SHORT).show()

            // is user has game, then go to ShowGameActivity, otherwise go to CreateGameActivity
            if (gameList.isNotEmpty()) {
                val intent = Intent(applicationContext, ShowGamesActivity::class.java)
                    .putExtra("GameList", gameList)
                    .putExtra("username", username)
                    .putExtra("password", password)
                    .putExtra("userpass", userpass)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, CreateGameActivity::class.java)
                    .putExtra("Username", username)
                    .putExtra("Password", password)
                startActivity(intent)
                finish()
            }
        }
    }

   /* private fun clearData(username: String, password: String){
        if(!sharedpreferences.contains(username)){
            Toast.makeText(this, "Error: User not found", Toast.LENGTH_SHORT).show()
        } else if(!sharedpreferences.getString(username, null).equals(password)) {
            Toast.makeText(this, "Error: Incorrect password", Toast.LENGTH_SHORT).show()
        } else{
            val editor = sharedpreferences.edit()
            editor.remove(username)
            editor.remove(username+password)
            editor.apply()
        }
        Toast.makeText(this, "User Data Cleared", Toast.LENGTH_SHORT).show()
    }
    */

    // check for valid passwords
    private fun validPassword(password: String?) : Boolean {
        return if (password.isNullOrEmpty()) {
            false
        } else {
            // Min 6 char, 1 letter, 1 number
            val passwordRegex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}\$")
            passwordRegex.matches(password)
        }
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



    companion object {
        val mypreference = "mypref"
    }
}