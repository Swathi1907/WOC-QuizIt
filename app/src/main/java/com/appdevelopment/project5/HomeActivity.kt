package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
 lateinit var pvQuizzes: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
       // Toast.makeText(this, "HomeActivity opened", Toast.LENGTH_SHORT).show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
val btnAttemptedQuizzes = findViewById<Button>(R.id.btnAttemptedQuizzes)
        val tvgreetings = findViewById<TextView>(R.id.tvgreetings)
        val btnCreateQuiz = findViewById<Button>(R.id.btnCreateQuiz)
        pvQuizzes = findViewById<RecyclerView>(R.id.PastQuizzes) //pv=previous
          val profile = findViewById<ImageView>(R.id.imageView2)
        val btnPerformances = findViewById<Button>(R.id.btnPerformances)
        pvQuizzes.layoutManager = LinearLayoutManager(this)
//
        val user = FirebaseAuth.getInstance().currentUser
        val username = user?.displayName?: "Champ"
        tvgreetings.text = "Hello, $username! "
profile.setOnClickListener {
    val intent = Intent(this, ProfileActivity::class.java)
    startActivity(intent)
}
btnAttemptedQuizzes.setOnClickListener {
    loadingQuizResults()
}
        btnPerformances.setOnClickListener{
            startActivity(Intent(this,PerformanceActivity::class.java))
        }
        btnCreateQuiz.setOnClickListener {
            val intent = Intent(this, QuizSetUpActivity::class.java)
            startActivity(intent)
        }
    }

        private fun loadingQuizResults() {
            lifecycleScope.launch {
                val results = withContext(Dispatchers.IO) {
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    AppDatabase.getDatabase(this@HomeActivity)
                        .quizResultDao()
                        .getAllResults(userId)
                }
                if (results.isEmpty()) {
                    Toast.makeText(this@HomeActivity, "No Past Quizzes", Toast.LENGTH_SHORT).show()
                } else {
                    pvQuizzes.adapter = QuizResultAdapter(results) { quizId ->
                            openQuizReview(quizId)}
                }
            }
        }
    private fun openQuizReview(quizId: Long){
        val intent = Intent(this,QuizReviewActivity::class.java)
        intent.putExtra("QUIZ_ID",quizId)
        startActivity(intent)
    }
    }
