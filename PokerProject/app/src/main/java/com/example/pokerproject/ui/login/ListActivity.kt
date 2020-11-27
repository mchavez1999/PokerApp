package com.example.pokerproject.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pokerproject.R
import java.time.LocalDateTime
import java.util.*

class ListActivity : AppCompatActivity() {
    private lateinit var recyclerview : RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(findViewById(R.id.toolbar))
        recyclerview = findViewById<RecyclerView>(R.id.recyclerView)
        val adapter1: RecyclerView.Adapter<MyAdapter.MyViewHolder> = MyAdapter(getListData())
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter1
    }
     fun getListData(): List<Game>{
        return listOf(Game(Game.status.COMPLETED, Game.game_type.TEXAS, Calendar.getInstance().time,  Calendar.getInstance().time, "Location", 5, 5, 5),
            Game(Game.status.COMPLETED, Game.game_type.SEVCARDSTUD, Calendar.getInstance().time,  Calendar.getInstance().time, "Location2", 5, 5, 5) )
    }
    }
