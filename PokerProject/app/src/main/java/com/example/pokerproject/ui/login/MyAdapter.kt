package com.example.pokerproject.ui.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pokerproject.R
import kotlinx.android.synthetic.main.recycler_view_item.view.*

class MyAdapter (private val games: List<Game>): RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MyViewHolder {

        val itemLayoutView: View =  LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_item, parent,false)

        val vh:MyViewHolder = MyViewHolder(itemLayoutView)
        return vh
    }
    override fun getItemCount(): Int {
        return games?.size ?: 0
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.date.text = games[position].startTime.toString()
        holder.itemView.location.text = games[position].location.toString()
        holder.itemView.gType.text = games[position].gtype.toString()
        holder.itemView.winnings.text = (games[position].cashOut - games[position].buyIn).toString()
    }


    class MyViewHolder(itemLayoutView: View):
    RecyclerView.ViewHolder(itemLayoutView) {

    }
}




