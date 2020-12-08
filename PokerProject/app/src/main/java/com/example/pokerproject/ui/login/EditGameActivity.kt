package com.example.pokerproject.ui.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
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
    inline fun Gson.fromJson(json: String) = fromJson<ArrayList<Game>>(
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
        gameList = json?.let { Gson().fromJson(it) }!!

        // get game
        var game = intent.getSerializableExtra("game") as Game
        gameID = intent.getSerializableExtra("ID") as Int

        // get all relevant fields
        val date = findViewById<TextView>(R.id.dateValue)
        val smallblind = findViewById<TextView>(R.id.smallBlindValue)
        val bigblind = findViewById<TextView>(R.id.bigBlindValue)
        val buyIn = findViewById<TextView>(R.id.buyinValue)
        val cashOut = findViewById<TextView>(R.id.cashoutValue)
        val location = findViewById<TextView>(R.id.locValue)
        val gameTypes = resources.getStringArray(R.array.GameTypes)
        val duration = findViewById<TextView>(R.id.durValue)
        val spinner = findViewById<Spinner>(R.id.spinner)
        var pos = 0
        if (spinner != null) {
            val adapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, gameTypes)
            spinner.adapter = adapter
            pos = adapter.getPosition(game.gameType)
            spinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>,
                                            view: View, position: Int, id: Long) {
                }
                override fun onNothingSelected(parent: AdapterView<*>) {
                }
            }
        }

        // set relevant fields
        date.text = game.date
        location.text = game.location
        spinner.setSelection(pos)
        duration.text = game.dur.toString()
        smallblind.text = game.smallBlind.toString()
        bigblind.text = game.bigBlind.toString()
        buyIn.text = game.buyin.toString()
        cashOut.text = game.cashout.toString()

        // get header and set it to display current game
        var header = findViewById<TextView>(R.id.buffer)
        header.text = "Editing Game ID: $gameID"

        // get buttons
        val submitBtn = findViewById<Button>(R.id.submit)
        val deleteBtn = findViewById<Button>(R.id.delete)

        // submit edits
        submitBtn.setOnClickListener {
            if (!dateValidator(date.text.toString())) {
                Toast.makeText(this, "Invalid Date Format.\nFormat: XX/XX/XXXX\nEX: 12/07/2020", Toast.LENGTH_LONG).show()
            } else if(!inputIsEmpty(date,location,duration,smallblind,bigblind,buyIn,cashOut)){
                submit(
                    date.text.toString(),
                    location.text.toString(),
                    duration.text.toString().toDouble(),
                    spinner.selectedItem.toString(),
                    smallblind.text.toString().toDouble(),
                    bigblind.text.toString().toDouble(),
                    buyIn.text.toString().toDouble(),
                    cashOut.text.toString().toDouble(),
                )

                // success game added toast
                Toast.makeText(this, "Successful Edits to Game ID: $gameID", Toast.LENGTH_SHORT).show()
            }
        }

        // delete game
        deleteBtn.setOnClickListener {
            delete()
        }
    }

    private fun submit(date: String, location: String, duration: Double, gametype: String, smallblind: Double, bigblind: Double, buyin: Double, cashout: Double){

        // search each game
        for (game in gameList) {

            // if we find our game we want to edit
            if (game.ID == gameID) {

                // set fields
                game.date = date
                game.location = location
                game.dur = duration
                game.gameType = gametype
                game.smallBlind = smallblind
                game.bigBlind = bigblind
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
                finish()
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
                    finish()
                } else {
                    Toast.makeText(this, "Deleted Game ID: $gameID", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, ShowGamesActivity::class.java)
                        .putExtra("GameList", gameList)
                        .putExtra("username", username)
                        .putExtra("password", password)
                        .putExtra("userpass", userpass)
                    startActivity(intent)
                    finish()
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

    private fun dateValidator(date: String) : Boolean {
        val dateRegex = Regex("^(0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])[- /.](19|20)\\d\\d\$")
        return dateRegex.matches(date)
    }

    private fun inputIsEmpty(date: TextView, location: TextView, duration: TextView, smallblind: TextView, bigblind: TextView, buyin: TextView, cashout: TextView): Boolean {
        return date.text.toString().trim().isEmpty() ||
                location.text.toString().trim().isEmpty() ||
                duration.text.toString().trim().isEmpty() ||
                smallblind.text.toString().trim().isEmpty() ||
                bigblind.text.toString().trim().isEmpty() ||
                buyin.text.toString().trim().isEmpty() ||
                cashout.text.toString().trim().isEmpty()
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, ShowGamesActivity::class.java)
            .putExtra("GameList", gameList)
            .putExtra("username", username)
            .putExtra("password", password)
            .putExtra("userpass", userpass)
        startActivity(intent)
        finish()
    }
}