package com.example.pokerproject.ui.login

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.SuperscriptSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.edmodo.rangebar.RangeBar
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener
import com.example.pokerproject.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.graph_view.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs
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
    lateinit var password: String
    lateinit var username: String
    lateinit var userpass: String


    var cPrimary: Int = 0
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
        range.setTickHeight(0f)
        val datRange: RangeBar = findViewById(R.id.rangebar2)
        datRange.setTickCount(50)
        datRange.setTickHeight(0f)
        tHold = findViewById(R.id.texas)
        draw = findViewById(R.id.draw)
        stud = findViewById(R.id.stud)
        omaha = findViewById(R.id.omaha)
        bphr = findViewById(R.id.bphr)
        varTv = findViewById(R.id.`var`)
        stdTv = findViewById(R.id.std)
        totalWon = findViewById(R.id.totalWon)
        brange = findViewById(R.id.bRange)
        drange = findViewById(R.id.bRange2)
        userpass = intent.getStringExtra("userpass") as String
        username = intent.getStringExtra("username") as String
        password = intent.getStringExtra("password") as String
        tHold.setOnCheckedChangeListener { buttonView, isChecked ->  updateGameTypes() }
        draw.setOnCheckedChangeListener{ buttonView, isChecked ->  updateGameTypes() }
        stud.setOnCheckedChangeListener{ buttonView, isChecked ->  updateGameTypes() }
        omaha.setOnCheckedChangeListener{ buttonView, isChecked ->  updateGameTypes() }
        gameList1 = intent.getSerializableExtra("gamelist") as ArrayList<Game>
        gameList = makePList(gameList1)
        cPrimary = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
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
        minSelectedBlind= minBlind
        maxSelectedBlind = maxBlind
        minSelectedDate = minDate
        maxSelectedDate = maxDate
        linechart = findViewById(R.id.lineChart)
        linechart2 = findViewById(R.id.lineChart2)
        val text: TextView = findViewById(R.id.TextBlindSize)
        // set an alternative background color
        linechart.setBackgroundColor(Color.TRANSPARENT)
        linechart2.setBackgroundColor(Color.TRANSPARENT)
        // add data
        updateGameTypes()
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
        if(plotList.size > 0) {
            val winnings = series2.get(series2.size - 1).y
            val blindsperhour = series1.get(series1.size - 1).y
            var std = 0f
            var variance = 0f
            for (item in series1) {
                variance += Math.pow((item.y - blindsperhour).toDouble(), 2.0).toFloat()
            }
            if (abs(variance) > 0.01f)
                variance = variance / (series1.size - 1)
            std = Math.pow(variance.toDouble(), .5).toFloat()
            val stdText ="Standard Deviation: " + std.toString(2) + " Blinds/hr"
            stdTv.text =stdText
            val varText = "Variance: " + variance.toString(2) + " Blinds2" + "/hr"
            val superscriptSpan = SuperscriptSpan()
            val builder = SpannableStringBuilder(varText)
            builder.setSpan(
                superscriptSpan,
                varText.indexOf("s2")+1,
                varText.indexOf("s2") + "s2".length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            `var`.text = builder
            val totText = "Total Winnings: $" + winnings.toString(2)
            totalWon.text = totText
            val blindsText = "Average Per Hour: " + blindsperhour.toString(2) +  " Big Blinds"
            bphr.text = blindsText
        }
        val v1 : LineDataSet= LineDataSet(series1, "Big Blinds per Hour")
        val v2 : LineDataSet = LineDataSet(series2, "Cumulative Winnings")
        v2.lineWidth = 3f
        v2.setDrawValues(false)
        v2.setDrawCircles(false)
        style(linechart, v1)
        style(linechart2, v2)
        linechart2.axisLeft.valueFormatter = formatter2

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
        return LocalDate.parse(date, dtFormat)
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
            return LocalDate.ofEpochDay(value.toLong()).format(DateTimeFormatter.ofPattern("MM/dd/yy"))
        }

    }
    var formatter2: ValueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return "$" + value.toString(0)
        }

    }
    class plottable(game: pGame){
        var day : Long = game.date.toEpochDay()
        var winnings: Float = ((game.cashout - game.buyin).toFloat())
        var bigBlindsperHour: Float = winnings/game.dur.toFloat()
    }
    private fun style(chart: LineChart, v: LineDataSet){
        v.lineWidth = 3f
        v.setDrawValues(false)
        v.setDrawCircles(false)
        v.color =  cPrimary
        chart.data = LineData(v)
        chart.axisRight.setDrawLabels(false)
        chart.xAxis.setDrawLabels(true)
        chart.legend.textSize = 14f
        chart.legend.typeface = Typeface.DEFAULT_BOLD
        chart.legend.textColor = cPrimary
        chart.xAxis.setLabelCount(4, true)
        chart.axisLeft.setLabelCount(4, true)
        chart.axisLeft.textSize = 12f
        chart.axisLeft.textColor = cPrimary
        chart.axisLeft.typeface = Typeface.DEFAULT_BOLD
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.textSize = 15f
        chart.xAxis.setAvoidFirstLastClipping(true)
        chart.xAxis.typeface =  Typeface.DEFAULT_BOLD
        chart.xAxis.gridColor = ResourcesCompat.getColor(resources, R.color.darkTransparent, null)
        chart.axisRight.gridColor = ResourcesCompat.getColor(
            resources,
            R.color.darkTransparent,
            null
        )
        chart.axisRight.gridLineWidth = 1f
        chart.xAxis.gridLineWidth = 1f
        chart.xAxis.textColor = cPrimary
        chart.xAxis.valueFormatter = formatter as ValueFormatter?
        chart.resetZoom()
        chart.description.isEnabled = false
    }
    override fun onBackPressed() {
        val intent = Intent(applicationContext, ShowGamesActivity::class.java)
            .putExtra("username", username)
            .putExtra("password", password)
            .putExtra("userpass", userpass)
            .putExtra("GameList", gameList1)

        startActivity(intent)
        finish()
    }

}