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
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class PerformanceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.performance)

        val lineChart = findViewById<LineChart>(R.id.lineChart)


        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                AppDatabase.getDatabase(this@PerformanceActivity)
                    .quizResultDao()
                    .getAllResults()
            }

            if (results.isNotEmpty()) {
                setupChart(lineChart, results)
            }
        }
    }

    private fun setupChart(
        chart: LineChart,
        results: List<QuizResultEntity>
    ) {
        val entries = ArrayList<Entry>()

        results.forEachIndexed { index, result ->
            entries.add(
                Entry(
                    index.toFloat(),
                    result.score.toFloat()
                )
            )
        }

        val dataSet = LineDataSet(entries, "Score")
        dataSet.color = Color.BLUE
        dataSet.setCircleColor(Color.BLUE)
        dataSet.valueTextSize = 12f
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 5f

        val lineData = LineData(dataSet)
        chart.data = lineData

        chart.description.text = "Quiz Performance"
        chart.setTouchEnabled(true)
        chart.setPinchZoom(true)

        chart.invalidate()// refresh graph
    }
}