package com.example.pokerproject.ui.login

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
    private lateinit var linechart : LineChart
    private lateinit var linechart2 : LineChart
    private lateinit var brange : TextView
    private lateinit var drange : TextView
    private lateinit var totalWon : TextView
    private lateinit var stdTv : TextView
    private lateinit var varTv : TextView
    private lateinit var bphr : TextView
    private lateinit var tHold: Chip
    private lateinit var omaha: Chip
    private lateinit var stud: Chip
    private lateinit var draw: Chip
    lateinit var password: String
    lateinit var username: String
    lateinit var userpass: String


    private var cPrimary: Int = 0
    private var minBlind: Float = Float.MAX_VALUE
    private var maxBlind: Float = Float.MIN_VALUE
    private var minDate : LocalDate = LocalDate.MAX
    private var maxDate : LocalDate = LocalDate.MIN
    private var minSelectedBlind: Float = Float.MAX_VALUE
    private var maxSelectedBlind: Float = Float.MIN_VALUE
    private var minSelectedDate : LocalDate = minDate
    private var maxSelectedDate : LocalDate = maxDate
    private val dtFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    private var selectedGameTypes : String = " "
    private lateinit var gameList: ArrayList<PGame>
    private lateinit var gameList1: ArrayList<Game>
    //overload float toString to trim number of decimals
    private fun Float.toString(numOfDec: Int): String {
        val integerDigits = this.toInt()
        val floatDigits = ((this - integerDigits) * 10f.pow(numOfDec)).roundToInt()
        return "${integerDigits}.${floatDigits}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_view)
        //range bar for blind size
        val range: RangeBar = findViewById<RangeBar>(R.id.rangebar)
        range.setTickCount(50)
        range.setTickHeight(0f)
        //range bar for date range
        val datRange: RangeBar = findViewById(R.id.rangebar2)
        datRange.setTickCount(50)
        datRange.setTickHeight(0f)
        //chips for game types
        tHold = findViewById(R.id.texas)
        draw = findViewById(R.id.draw)
        stud = findViewById(R.id.stud)
        omaha = findViewById(R.id.omaha)
        //textviews for summary statistics
        bphr = findViewById(R.id.bphr)
        varTv = findViewById(R.id.`var`)
        stdTv = findViewById(R.id.std)
        totalWon = findViewById(R.id.totalWon)
        //text view for selected blind range and date range
        brange = findViewById(R.id.bRange)
        drange = findViewById(R.id.bRange2)
        //intents passed by showGamesActivity
        userpass = intent.getStringExtra("userpass") as String
        username = intent.getStringExtra("username") as String
        password = intent.getStringExtra("password") as String
        //adding listeners to each chip
        tHold.setOnCheckedChangeListener { _, _ ->  updateGameTypes() }
        draw.setOnCheckedChangeListener{ _, _ ->  updateGameTypes() }
        stud.setOnCheckedChangeListener{ _, _ -> updateGameTypes() }
        omaha.setOnCheckedChangeListener{ _, _ ->  updateGameTypes() }
        //getting game list from intent
        gameList1 = intent.getSerializableExtra("gamelist") as ArrayList<Game>
        //making list of type pGame which converts date string to a date object, and drops
        // uneeded values, also makePlist sorts by date
        gameList = makePList(gameList1)
        //getting primary color from resources
        cPrimary = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
        //finding max and min dates and blinds
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
        //setting initial selections to full range
        minSelectedBlind= minBlind
        maxSelectedBlind = maxBlind
        minSelectedDate = minDate
        maxSelectedDate = maxDate
        //linecharts from MPAndroidChart
        linechart = findViewById(R.id.lineChart)
        linechart2 = findViewById(R.id.lineChart2)
        // transparent background for charts
        linechart.setBackgroundColor(Color.TRANSPARENT)
        linechart2.setBackgroundColor(Color.TRANSPARENT)
        //update game types also plots initial values
        updateGameTypes()
        //listener for both rangebars, updated max and min values and replots
        range.setOnRangeBarChangeListener(OnRangeBarChangeListener { _, leftThumbIndex, rightThumbIndex ->
            val lowerBlind: Float = ((maxBlind - minBlind) / 49) * leftThumbIndex + minBlind
            val uppBlind: Float = (((maxBlind - minBlind) / 49) * rightThumbIndex + minBlind)
            maxSelectedBlind = uppBlind
            minSelectedBlind = lowerBlind
            plot()
        })
        datRange.setOnRangeBarChangeListener(OnRangeBarChangeListener { _, leftThumbIndex, rightThumbIndex ->
            minSelectedDate =
                LocalDate.ofEpochDay((maxDate.toEpochDay() - minDate.toEpochDay()) / 49 * leftThumbIndex + minDate.toEpochDay() - 20)
            maxSelectedDate =
                LocalDate.ofEpochDay(+(maxDate.toEpochDay() - minDate.toEpochDay()) / 47 * rightThumbIndex + minDate.toEpochDay())
            plot()
        })
    }
    //check which chips are checked and adds them to selectedGameTypes string
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
 // function to plot graphs
    private fun plot() {
        //text for blind ranges and date ranges set
        val blindText = "$${minSelectedBlind.toString(2)} --- $${maxSelectedBlind.toString(2)}"
        brange.text = blindText
        val dateText = minSelectedDate.format(dtFormat) + " --- " + maxSelectedDate.format(dtFormat)
        drange.text = dateText
        //pGameList from gameList
        gameList = makePList(gameList1)
       //games with blinds or dates outside range removed
        gameList = filter(gameList)
        //list of plottable objects, just day in epoch days, blinds/hr, and total winnings
        val plotList = plottableList(gameList)
        //making series to be plotted for avgBlinds/hr as well as cumulative winnnings
        val series1: ArrayList<Entry> = avgBlindshrEntries(plotList)
        val series2: ArrayList<Entry> = cumulativeWinningsEntries(plotList)
        //if there are entries than calculate statistics
        if(plotList.size > 0) {
            val winnings = series2.get(series2.size - 1).y
            val blindsperhour = series1.get(series1.size - 1).y
            var std :Float
            var variance = 0f
            for (item in series1) {
                //variance before division
                variance += Math.pow((item.y - blindsperhour).toDouble(), 2.0).toFloat()
            }
            if (abs(variance) > 0.01f)
                //if variance is not 0 (n = 1), divide by n-1
                variance /= (series1.size - 1)
            //std = Sqrt(var)
            std = Math.pow(variance.toDouble(), .5).toFloat()
            //text for statistics
            val stdText ="Standard Deviation: " + std.toString(2) + " Blinds/hr"
            stdTv.text =stdText
            val varText = "Variance: " + variance.toString(2) + " Blinds2" + "/hr"
            val superscriptSpan = SuperscriptSpan()
            //superscript builder since units of variane is blinds squared
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
            val blindsText = "Average Per Hour: " + blindsperhour.toString(2) +  "Big Blinds"
            bphr.text = blindsText
        }
        //line data set from the two data series
        val v1 : LineDataSet= LineDataSet(series1, "Big Blinds per Hour")
        val v2 : LineDataSet = LineDataSet(series2, "Cumulative Winnings")
        //style function plots both series and sets the style
        style(linechart, v1)
        style(linechart2, v2)
         //dollar sign formatter for total winnings plot
        linechart2.axisLeft.valueFormatter = formatter2

    }
    //filters games out of set range from list
    private fun filter(gameList: ArrayList<PGame>): ArrayList<PGame> {
        val todel = arrayListOf<PGame>()
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
        //remove marked games
        gameList.removeAll(todel)
        return gameList
    }
    // making games pGames, sorting, and returning list
    private fun makePList(list: ArrayList<Game>) : ArrayList<PGame>{
        var ret: ArrayList<PGame> = arrayListOf<PGame>()
        for (item in list){
            ret.add(PGame(item))
        }
        ret.sortBy { t -> t.date }
        return ret
    }
    // turning pGames into plottable adn returning
    private fun plottableList(input: ArrayList<PGame>)  : ArrayList<plottable>{
        var ret :ArrayList<plottable> = arrayListOf<plottable>()
        for(item in input){
            ret.add(plottable(item))
        }
        return ret
    }
    //calculating cumulative average blinds per hour and returning a list of Entry object for plotting
    private fun avgBlindshrEntries(lst: ArrayList<plottable>) : ArrayList<Entry>{
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
    //cumulative total winning calculated and returned as entry list
    private fun cumulativeWinningsEntries(lst: ArrayList<plottable>) : ArrayList<Entry>{
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
    //date formatter for both plots
    private var formatter: ValueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return LocalDate.ofEpochDay(value.toLong()).format(DateTimeFormatter.ofPattern("MM/dd/yy"))
        }

    }
    //dollar sign formatter
    private var formatter2: ValueFormatter = object : ValueFormatter() {
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return "$" + value.toString(0)
        }

    }
    //plottable class further ruduces pgame into only neccesary values
   private class plottable(game: PGame){
        var day : Long = game.date.toEpochDay()
        var winnings: Float = ((game.cashout - game.buyin).toFloat())
        var bigBlindsperHour: Float = winnings/game.dur.toFloat()
    }
    //style function draws given data set on given chart
    private fun style(chart: LineChart, v: LineDataSet){
        v.lineWidth = 3f
        v.setDrawValues(false)
        v.setDrawCircles(false)
        //linecolor
        v.color =  cPrimary
        //adding data to chart
        chart.data = LineData(v)
        //remove right axis
        chart.axisRight.setDrawLabels(false)
        chart.xAxis.setDrawLabels(true)
        chart.legend.textSize = 15f
        chart.legend.typeface = Typeface.DEFAULT_BOLD
        chart.legend.textColor = cPrimary
        //forcing number of labels to four, so there is enough space
        chart.xAxis.setLabelCount(4, true)
        chart.axisLeft.setLabelCount(4, true)
        chart.axisLeft.textSize = 13f
        chart.axisLeft.textColor = cPrimary
        chart.axisLeft.typeface = Typeface.DEFAULT_BOLD
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.textSize = 15f
        //forces xAxis labels to interior of chart
        chart.xAxis.setAvoidFirstLastClipping(true)
        chart.xAxis.typeface =  Typeface.DEFAULT_BOLD
        //setting color of grid
        chart.xAxis.gridColor = ResourcesCompat.getColor(resources, R.color.darkTransparent, null)
        chart.axisRight.gridColor = ResourcesCompat.getColor(
            resources,
            R.color.darkTransparent,
            null
        )
        chart.axisRight.gridLineWidth = 1f
        chart.xAxis.gridLineWidth = 1f
        chart.xAxis.textColor = cPrimary
        //formatting dates
        chart.xAxis.valueFormatter = formatter
        chart.resetZoom()
        // removing descriptions
        chart.description.isEnabled = false
    }

    //when back buttom is pressed return to game list
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