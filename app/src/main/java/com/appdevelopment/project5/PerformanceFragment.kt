package com.appdevelopment.project5

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class PerformanceFragment : Fragment(R.layout.performance) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val barChart = view.findViewById<BarChart>(R.id.barChart)
        loadPerformanceFromFirestore(barChart)
    }

    private fun loadPerformanceFromFirestore(chart: BarChart) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("pastQuizzes")
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) return@addOnSuccessListener

                val results = snapshot.documents.map {
                    PerformanceItem(
                        score = it.getLong("score")?.toInt() ?: 0,
                        totalQuestion = it.getLong("totalQuestions")?.toInt() ?: 0,
                        timestamp = it.getLong("timestamp") ?: 0L
                    )
                }

                setupChart(chart, results)
            }
    }
    private fun setupChart(
        chart: BarChart,
        results: List<PerformanceItem>
    ) {
        val entries = ArrayList<BarEntry>() // bar heights (scores)
        val dateLabels = ArrayList<String>() // x axis labels

        val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault()) // converts time stamp to date
// converts db data to chart bars
        results.forEachIndexed { index, result -> // x position is index
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
// creates bar group named "Score"
        val dataSet = BarDataSet(entries, "Score")
        dataSet.color = Color.parseColor("#14B8A6") // sea green
        dataSet.valueTextSize = 15f

        //  Show score/total on top of bar
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getBarLabel(barEntry: BarEntry?): String {
                val index = barEntry?.x?.toInt() ?: 0
                val r = results[index]
                return "${r.score}/${r.totalQuestion}"

            }
        }
// attach data
        val barData = BarData(dataSet) // wraps dataset into chart data
        barData.barWidth = 0.3f
        chart.data = barData

        //  X AXIS (DATES)
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f // one label per bar
            //granularity means the step size btw values shown on axis
            setDrawGridLines(false) // vertical lines are hidden
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return dateLabels.getOrNull(value.toInt()) ?: ""
                }
            }
        }



        chart.axisRight.isEnabled = false
        chart.description.text = "Quiz Performance"
        chart.setFitBars(true) // adjust bars spacing
        // Find highest score
        val maxScore = results.maxOf { it.score }   // or totalQuestions

// Y-axis configuration (GRAPH HEIGHT)
        chart.axisLeft.apply {
            axisMinimum = 0f // starts from 0
            axisMaximum = (maxScore + 7).toFloat()   ///////// MAX HEIGHT HERE
            granularity = 1f
            isGranularityEnabled = true
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return value.toInt().toString()
                }
            }
        }
        chart.invalidate() // refreshes and redraws the chart
    }
}