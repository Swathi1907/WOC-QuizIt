package com.appdevelopment.project5

import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appdevelopment.project5.room.AppDatabase
import com.appdevelopment.project5.room.QuizResultEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth

class PerformanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.performance)

        val barChart = findViewById<BarChart>(R.id.barChart)


        lifecycleScope.launch {
            val results: List<QuizResultEntity> = withContext(Dispatchers.IO) {
              val db=  AppDatabase.getDatabase(this@PerformanceActivity)
                val userId = FirebaseAuth.getInstance().currentUser!!.uid ?: return@withContext emptyList()
                   db .quizResultDao()
                    .getAllResults(userId)
            }

            if (results.isNotEmpty()) {
                setupChart(barChart, results)
            }
        }
    }
//
private fun setupChart(
    chart: BarChart,
    results: List<QuizResultEntity>
) {
    val entries = ArrayList<BarEntry>()
    val dateLabels = ArrayList<String>()

    val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    results.forEachIndexed { index, result ->
        // bar height = score
        entries.add(
            BarEntry(
                index.toFloat(),
                result.score.toFloat()
            )
        )

        // x-axis label = date
        dateLabels.add(dateFormat.format(Date(result.timestamp)))
    }

    val dataSet = BarDataSet(entries, "Score")
    dataSet.color = Color.parseColor("#00ACC1") // blue
    dataSet.valueTextSize = 12f

    //  Show score/total on top of bar
    dataSet.valueFormatter = object : ValueFormatter() {
        override fun getBarLabel(barEntry: BarEntry?): String {
            val index = barEntry?.x?.toInt() ?: 0
            val r = results[index]
            return "${r.score}/${r.totalQuestions}"

        }
    }

    val barData = BarData(dataSet)
    barData.barWidth = 0.3f
    chart.data = barData

    // ===== X AXIS (DATES) =====
    chart.xAxis.apply {
        position = XAxis.XAxisPosition.BOTTOM
        granularity = 1f
        setDrawGridLines(false)
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return dateLabels.getOrNull(value.toInt()) ?: ""
            }
        }
    }

    // ===== Y AXIS (INTEGERS ONLY) =====
   // chart.axisLeft.apply {
     //   granularity = 1f
       // isGranularityEnabled = true
      //  axisMinimum = 0f
        //valueFormatter = object : ValueFormatter() {
          //  override fun getFormattedValue(value: Float): String {
           //     return value.toInt().toString()
         //   }
        //}
    //}

    chart.axisRight.isEnabled = false
    chart.description.text = "Quiz Performance"
    chart.setFitBars(true)
    // Find highest score
    val maxScore = results.maxOf { it.score }   // or totalQuestions

// Y-axis configuration (GRAPH HEIGHT)
    chart.axisLeft.apply {
        axisMinimum = 0f
        axisMaximum = (maxScore + 7).toFloat()   // 👈 MAX HEIGHT HERE
        granularity = 1f
        isGranularityEnabled = true
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
    }
    chart.invalidate()
}
}