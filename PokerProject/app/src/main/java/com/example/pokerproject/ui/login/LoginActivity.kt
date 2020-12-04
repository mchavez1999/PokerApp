package com.example.pokerproject.ui.login

import android.content.Context
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

class LoginActivity : AppCompatActivity() {
    lateinit var sharedpreferences: SharedPreferences

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

        val clear = findViewById<Button>(R.id.clear) as Button
        clear.setOnClickListener(){
            clearData()
        }
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
        }else if(!sharedpreferences.getString(username, null).equals(password)) {
            Toast.makeText(this, "Error: Incorrect password", Toast.LENGTH_SHORT).show()
        }else{
            val intent = Intent(this, CreateGameActivity::class.java).putExtra("Username", username).putExtra("Password", password)
            startActivity(intent)
        }
    }

    private fun clearData(){
        sharedpreferences.edit().clear().commit();
        Toast.makeText(this, "User Data Cleared", Toast.LENGTH_SHORT).show()
    }

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



    companion object {
        val mypreference = "mypref"
    }
}