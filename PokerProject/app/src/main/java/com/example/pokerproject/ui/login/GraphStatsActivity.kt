package com.example.pokerproject.ui.login

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.edmodo.rangebar.RangeBar
import com.edmodo.rangebar.RangeBar.OnRangeBarChangeListener
import com.example.pokerproject.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.graph_view.*
import java.util.*
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import kotlin.math.pow
import kotlin.math.roundToInt


class GraphStatsActivity : AppCompatActivity() {
    lateinit var linechart : LineChart
    val entries = ArrayList<Entry>()
    var minBlind: Float = 0f
    var maxBlind: Float = 100f
    var minDate : Date = Calendar.getInstance().time
    var maxDate : Date = Calendar.getInstance().time
    var username: String = "help"
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
        title = "LineChartTime"
        linechart = findViewById(R.id.lineChart)
        val text: TextView = findViewById(R.id.TextBlindSize)
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))
        entries.add(Entry(4f, 20f))
        entries.add(Entry(5f, 16f))
        // no description text
        linechart.description.isEnabled = false
        val vl = LineDataSet(entries, "My Type")
        // enable touch gestures
        linechart.setTouchEnabled(true)
        vl.setDrawValues(false)
        vl.setDrawFilled(true)
        vl.lineWidth = 3f
        vl.fillColor = R.color.color1
        vl.fillAlpha = R.color.color2

//Part5
        linechart.xAxis.labelRotationAngle = 0f

//Part6
        linechart.data = LineData(vl)
        linechart.dragDecelerationFrictionCoef = 0.9f

        // enable scaling and dragging
        linechart.isDragEnabled = true
        linechart.setScaleEnabled(true)
        linechart.setDrawGridBackground(false)
        linechart.isHighlightPerDragEnabled = true

        // set an alternative background color
        linechart.setBackgroundColor(Color.WHITE)
        linechart.setViewPortOffsets(0f, 0f, 0f, 0f)

        // add data
        range.setOnRangeBarChangeListener(OnRangeBarChangeListener { range, leftThumbIndex, rightThumbIndex ->
            val lowerBlind :Float = ((maxBlind - minBlind)/49) * leftThumbIndex + minBlind
            val uppBlind: Float = (((maxBlind - minBlind)/49) * rightThumbIndex + minBlind)
            val txt : String = "\$${lowerBlind.toString(2)},  \$${uppBlind.toString(2)}"
            text.text = txt
        })
        datRange.setOnRangeBarChangeListener(OnRangeBarChangeListener { range, leftThumbIndex, rightThumbIndex ->
            //val lowerBlind :Float = ((maxDate.until(minDate) minDate)/49) * leftThumbIndex + minBlind
            //val uppBlind: Float = (((maxBlind - minBlind)/49) * rightThumbIndex + minBlind)
            //val txt : String = "\$${lowerBlind.toString(2)},  \$${uppBlind.toString(2)}"
           // text.text = txt
        })
        // get the legend (only possible after setting data)
    }

}