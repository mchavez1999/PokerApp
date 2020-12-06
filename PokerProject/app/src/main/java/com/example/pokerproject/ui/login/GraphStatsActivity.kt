package com.example.pokerproject.ui.login

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edmodo.rangebar.RangeBar
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener
import com.example.pokerproject.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.android.synthetic.main.graph_view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.roundToInt


class GraphStatsActivity : AppCompatActivity() {
    lateinit var linechart : LineChart
    lateinit var linechart2 : LineChart
    lateinit var stardDatetv : TextView
    lateinit var endDatetv : TextView
    lateinit var brange : TextView
    lateinit var drange : TextView
    lateinit var totalWon : TextView
    lateinit var stdTv : TextView
    lateinit var varTv : TextView
    lateinit var bphr : TextView
    lateinit var tHold: Chip
    lateinit var omaha: Chip
    lateinit var stud: Chip
    lateinit var draw: Chip

    lateinit var chipGroup: ChipGroup
    val entries = ArrayList<Entry>()
    var minBlind: Float = Float.MAX_VALUE
    var maxBlind: Float = Float.MIN_VALUE
    var minDate : LocalDate = LocalDate.MAX
    var maxDate : LocalDate = LocalDate.MIN
    var minSelectedBlind: Float = Float.MAX_VALUE
    var maxSelectedBlind: Float = Float.MIN_VALUE
    var minSelectedDate : LocalDate = minDate
    var maxSelectedDate : LocalDate = maxDate
    val dtFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    var selectedGameTypes : String = " "
    lateinit var gameList: ArrayList<pGame>
    lateinit var gameList1: ArrayList<Game>
    fun Float.toString(numOfDec: Int): String {
        val integerDigits = this.toInt()
        val floatDigits = ((this - integerDigits) * 10f.pow(numOfDec)).roundToInt()
        return "${integerDigits}.${floatDigits}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_view)
        val range: RangeBar = findViewById<RangeBar>(R.id.rangebar)
        range.setTickCount(50)
        range.setTickHeight(1.5F)
        val datRange: RangeBar = findViewById(R.id.rangebar2)
        datRange.setTickCount(50)
        tHold = findViewById(R.id.texas)
        draw = findViewById(R.id.draw)
        stud = findViewById(R.id.stud)
        omaha = findViewById(R.id.omaha)
        chipGroup = findViewById(R.id.help)
        brange = findViewById(R.id.bRange)
        drange = findViewById(R.id.bRange2)
        tHold.setOnCheckedChangeListener { buttonView, isChecked ->  updateGameTypes() }
        draw.setOnCheckedChangeListener{ buttonView, isChecked ->  updateGameTypes() }
        stud.setOnCheckedChangeListener{ buttonView, isChecked ->  updateGameTypes() }
        omaha.setOnCheckedChangeListener{ buttonView, isChecked ->  updateGameTypes() }
        title = "LineChartTime"
        gameList1 = intent.getSerializableExtra("gamelist") as ArrayList<Game>
        gameList = makePList(gameList1)
        for(g in gameList){
            if(g.bigBlind > maxBlind)
                maxBlind = g.bigBlind.toFloat()
            if(g.bigBlind < minBlind)
                minBlind = g.bigBlind.toFloat()
            if(g.date < minDate)
                minDate = g.date
            if(g.date > maxDate)
                maxDate = g.date
        }
        linechart = findViewById(R.id.lineChart)
        linechart2 = findViewById(R.id.lineChart2)
        val text: TextView = findViewById(R.id.TextBlindSize)
        // set an alternative background color
        linechart.setBackgroundColor(Color.WHITE)
        linechart2.setBackgroundColor(Color.WHITE)
        // add data
        range.setOnRangeBarChangeListener(OnRangeBarChangeListener { range, leftThumbIndex, rightThumbIndex ->
            val lowerBlind: Float = ((maxBlind - minBlind) / 49) * leftThumbIndex + minBlind
            val uppBlind: Float = (((maxBlind - minBlind) / 49) * rightThumbIndex + minBlind)
            maxSelectedBlind = uppBlind
            minSelectedBlind = lowerBlind
            plot()
        })
        datRange.setOnRangeBarChangeListener(OnRangeBarChangeListener { range, leftThumbIndex, rightThumbIndex ->
            minSelectedDate =
                LocalDate.ofEpochDay((maxDate.toEpochDay() - minDate.toEpochDay()) / 49 * leftThumbIndex + minDate.toEpochDay() - 20)
            maxSelectedDate =
                LocalDate.ofEpochDay(+(maxDate.toEpochDay() - minDate.toEpochDay()) / 47 * rightThumbIndex + minDate.toEpochDay())
            plot()
        })
    }

    private fun updateGameTypes() {
        selectedGameTypes = " "
        if(omaha.isChecked)
            selectedGameTypes+= "Pot Limit Omaha"
        if(tHold.isChecked)
            selectedGameTypes+= "Texas Hold'Em"
        if(stud.isChecked)
            selectedGameTypes+= "Seven Card Stud"
        if(draw.isChecked)
            selectedGameTypes+= "2-7 Triple Draw"
        plot()
    }

    private fun plot() {
        val blindText = "$${minSelectedBlind.toString(2)} --- $${maxSelectedBlind.toString(2)}"
        brange.text = blindText
        val dateText = minSelectedDate.format(dtFormat) + " --- " + maxSelectedDate.format(dtFormat)
        drange.text = dateText
        gameList = makePList(gameList1)
        gameList = filter(gameList)
        val plotList = plottableList(gameList)
        val series1: ArrayList<Entry> = avgBlindshrEntries(plotList)
        val series2: ArrayList<Entry> = cumulativeWinningsEntries(plotList)
        val v1 = LineDataSet(series1, "Big Blinds per Hour")
        val v2 = LineDataSet(series2, "Cumulative Winnings")
        linechart.data = LineData(v1)
        linechart2.data = LineData(v2)
        linechart.resetZoom()
        linechart.axisRight.setDrawLabels(false)
        linechart.xAxis.setDrawLabels(false)
        linechart2.axisRight.setDrawLabels(false)
        linechart2.xAxis.setDrawLabels(true)
        linechart2.xAxis.setLabelCount(5, true)
        linechart2.xAxis.valueFormatter = formatter as ValueFormatter?
        linechart2.resetZoom()
        linechart.description.isEnabled = false
        lineChart2.description.isEnabled = false

    }

    private fun filter(gameList: ArrayList<pGame>): ArrayList<pGame> {
        val todel = arrayListOf<pGame>()
        for(item in gameList){
            if(item.bigBlind < minSelectedBlind || item.bigBlind > maxSelectedBlind){
                todel.add(item)
                continue
            }
            if(item.date < minSelectedDate || item.date > maxSelectedDate){
                todel.add(item)
                continue
            }
            if(!selectedGameTypes.contains(item.gameType)){
                todel.add(item)
            }
        }
        gameList.removeAll(todel)
        if(gameList.size != 0) {
            //stardDatetv.text = gameList.get(0).date.format(dtFormat)
         //   endDatetv.text =
         //       gameList.get(gameList.size - 1).date.format(dtFormat)
        }
        return gameList
    }

    private fun makePList(list: ArrayList<Game>) : ArrayList<pGame>{
        var ret: ArrayList<pGame> = arrayListOf<pGame>()
        for (item in list){
            ret.add(pGame(item))
        }
        ret.sortBy { t -> t.date }
        return ret
    }
    fun toLocalDate(date: String): LocalDate{
        return LocalDate.parse(date,dtFormat)
    }
    fun plottableList(input: ArrayList<pGame>)  : ArrayList<plottable>{
        var ret :ArrayList<plottable> = arrayListOf<plottable>()
        for(item in input){
            ret.add(plottable(item))
        }
        return ret
    }
    fun avgBlindshrEntries(lst: ArrayList<plottable>) : ArrayList<Entry>{
        val len = lst.size
        var ret: ArrayList<Entry> = arrayListOf<Entry>()
        for(i in 0 until len){
            if(i == 0){
                ret.add(Entry(lst[i].day.toFloat(), lst[i].bigBlindsperHour))
                continue
            }
            val avg = (ret[i - 1].y*i + lst.get(i).bigBlindsperHour)/(i+1)
            ret.add(Entry(lst[i].day.toFloat(), avg))
        }
        return ret
    }
    fun cumulativeWinningsEntries(lst: ArrayList<plottable>) : ArrayList<Entry>{
        val len = lst.size
        var ret: ArrayList<Entry> = arrayListOf<Entry>()
        for(i in 0 until len){
            if(i == 0){
                ret.add(Entry(lst[i].day.toFloat(), lst[i].winnings))
                continue
            }
            val winnings = (ret[i - 1].y + lst.get(i).winnings)
            ret.add(Entry(lst[i].day.toFloat(), winnings))
        }
        return ret
    }
    var formatter: ValueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return LocalDate.ofEpochDay(value.toLong()).format(dtFormat)
        }

    }
    class plottable(game: pGame){
        var day : Long = game.date.toEpochDay()
        var winnings: Float = ((game.cashout - game.buyin).toFloat())
        var bigBlindsperHour: Float = winnings/game.dur.toFloat()
    }

}