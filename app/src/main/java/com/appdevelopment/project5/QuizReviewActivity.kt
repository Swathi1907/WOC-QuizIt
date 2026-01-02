package com.appdevelopment.project5

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class QuizReviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_review)
val rv = findViewById<RecyclerView>(R.id.rvReview)
        rv.layoutManager = LinearLayoutManager(this)
        val quizId = intent.getLongExtra("QUIZ_ID", -1)

        lifecycleScope.launch(Dispatchers.IO) {
            val questions =
                AppDatabase.getDatabase(this@QuizReviewActivity)
                    .quizDao()
                    .getQuestionsForQuiz(quizId)

            withContext(Dispatchers.Main) {
                rv.adapter =
                    ReviewAdapter(questions)
            }
        }
    }
}