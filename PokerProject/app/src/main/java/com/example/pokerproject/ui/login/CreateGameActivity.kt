package com.example.pokerproject.ui.login


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pokerproject.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random


class CreateGameActivity : AppCompatActivity() {

    lateinit var sharedpreferences: SharedPreferences
    lateinit var userpass: String
    lateinit var gameList: ArrayList<Game>
    inline fun <reified T> Gson.fromJson(json: String) = fromJson<ArrayList<Game>>(
        json,
        object : TypeToken<ArrayList<Game>>() {}.type
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // game parameter inputs
        val date = findViewById<TextView>(R.id.dateValue)
        val smallblind = findViewById<TextView>(R.id.smallBlindValue)
        val bigblind = findViewById<TextView>(R.id.bigBlindValue)
        val buyin = findViewById<TextView>(R.id.buyinValue)
        val cashout = findViewById<TextView>(R.id.cashoutValue)
        val location = findViewById<TextView>(R.id.locValue)
        val gameTypes = resources.getStringArray(R.array.GameTypes)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val duration = findViewById<TextView>(R.id.durValue)

        // username and password
        val username = intent.getStringExtra("Username")
        val password = intent.getStringExtra("Password")
        userpass = username+password

        //Setting Game Type Selector
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, gameTypes)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        // get and build JSON
        sharedpreferences = getSharedPreferences(LoginActivity.mypreference, Context.MODE_PRIVATE)
        var json = sharedpreferences.getString(userpass, null)
        gameList = json?.let { Gson().fromJson<Game>(it) }!!

        // addGame button functionality
        val addGame = findViewById<Button>(R.id.addGame) as Button
        addGame.setOnClickListener{

            // if date is in a valid format, add the game!
            if (!dateValidator(date.text.toString())) {
                Toast.makeText(this, "Invalid Date Format.\nFormat: XX/XX/XXXX\nEX: 12/07/2020", Toast.LENGTH_LONG).show()
            } else if(date.text.toString().trim().isNotEmpty() && location.text.toString().trim().isNotEmpty() && duration.text.toString().trim().isNotEmpty() && smallblind.text.toString().trim().isNotEmpty() && buyin.text.toString().trim().isNotEmpty() && cashout.text.toString().trim().isNotEmpty()){
                add(
                    date.text.toString(),
                    location.text.toString(),
                    duration.text.toString().toDouble(),
                    spinner.selectedItem.toString(),
                    smallblind.text.toString().toDouble(),
                    bigblind.text.toString().toDouble(),
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
                finish()
            } else {
                Toast.makeText(this, "No Games Left.\nAdd Game(s).", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun nextID(): Int {
        if(gameList.isNotEmpty()){
            return gameList.get(gameList.size-1).ID + 1
        }
        return 1
    }

    private fun add(date: String, location: String, duration: Double, gametype: String, smallblind: Double, bigblind: Double, buyin: Double, cashout: Double){
        gameList.add(Game(date, location, duration, gametype, smallblind, bigblind, buyin, cashout, nextID()))
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
