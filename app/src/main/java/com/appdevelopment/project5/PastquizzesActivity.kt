
package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PastQuizzesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pastquizzzes)

        recyclerView = findViewById(R.id.PastQuizzes)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadPastQuizzes()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_past -> {


                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }




                else -> false
            }
        }

    }
    private fun loadPastQuizzes() {
        lifecycleScope.launch {
            val results = withContext(Dispatchers.IO) {
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                AppDatabase.getDatabase(this@PastQuizzesActivity)
                    .quizResultDao()
                    .getAllResults(userId)
            }

            if (results.isEmpty()) {
                Toast.makeText(this@PastQuizzesActivity,
                    "No past quizzes found", Toast.LENGTH_SHORT).show()
            } else {
                recyclerView.adapter =
                    QuizResultAdapter(results) { quizId ->
                        openQuizReview(quizId)
                    }
            }
        }
    }

    private fun openQuizReview(quizId: Long) {
        val intent = Intent(this, QuizReviewActivity::class.java)
        intent.putExtra("QUIZ_ID", quizId)
        startActivity(intent)
    }
}

