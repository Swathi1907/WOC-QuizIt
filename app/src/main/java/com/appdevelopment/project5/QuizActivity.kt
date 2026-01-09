package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.appdevelopment.project5.room.AppDatabase
import com.appdevelopment.project5.room.QuizQuestionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.appdevelopment.project5.room.QuizResultEntity
import com.google.firebase.auth.FirebaseAuth

class QuizActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var btnBefore: Button
    private lateinit var btnNext: Button
    private lateinit var btnSubmit: Button
    private lateinit var tvTitle: TextView
    private lateinit var quiztitle: String
private lateinit var tvtimer: TextView
private var countDownTimer: CountDownTimer? = null

    private var timeLimitMillis: Long = 0L

    private var quizId: Long = 0L
    private lateinit var adapter: QuestionPagerAdapter
    private var questions: List<QuizQuestionEntity> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
      //  Toast.makeText(this,"QuizActivity opened",Toast.LENGTH_SHORT).show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)
        tvtimer = findViewById(R.id.tvTimer)

        val timeLimitMinutes = intent.getIntExtra("TIME_LIMIT", 5)
        timeLimitMillis = timeLimitMinutes * 60 * 1000L
Log.d("QuizSetUpActivity","time = $timeLimitMinutes")
        startTimer()
        // findViewById for all views
        viewPager = findViewById(R.id.viewPagerQuestions)
        btnBefore = findViewById(R.id.btnBefore)
        btnNext = findViewById(R.id.btnNext)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvTitle = findViewById<TextView>(R.id.tvQuizTitle)

        // get quiz id passed from MainActivity
        quizId = intent.getLongExtra("QUIZ_ID", 0L)

     //   tvTitle = intent.getStringExtra(quiztitle).toString()
        // load questions from DB and setup UI
        lifecycleScope.launch {
            questions = loadQuestionsFromDB(quizId)

            if (questions.isEmpty()) {
                AlertDialog.Builder(this@QuizActivity)
                    .setTitle("No questions")
                    .setMessage("Could not load any questions for this quiz.")
                    .setPositiveButton("OK") { d, _ -> d.dismiss(); finish() }
                    .show()
                return@launch
            }

            // optionally set title to show number of questions
            tvTitle.text = "Quiz - ${questions.size}Q's"

            // create and set adapter
            adapter = QuestionPagerAdapter(questions, this@QuizActivity)
            viewPager.adapter = adapter

            // Prev button: go back one page if possible
            btnBefore.setOnClickListener {
                val cur = viewPager.currentItem
                if (cur > 0) viewPager.currentItem = cur - 1
            }

            // Next button: go forward or stay at last
            btnNext.setOnClickListener {
                val cur = viewPager.currentItem
                if (cur < adapter.itemCount - 1) {
                    viewPager.currentItem = cur + 1
                }
            }

            // Submit button: calculate score and show result
            btnSubmit.setOnClickListener {
                val score = adapter.calculateScore()
lifecycleScope.launch(Dispatchers.IO) {
    val userId = FirebaseAuth.getInstance().currentUser!!.uid
    val result = QuizResultEntity(
        quizId = quizId,
        score = score,
        totalQuestions = adapter.itemCount,
        userId = userId,

    )
    AppDatabase.getDatabase(this@QuizActivity)
        .quizResultDao()
        .insertResult(result)
}
                AlertDialog.Builder(this@QuizActivity)
                    .setTitle("Quiz Completed")
                    .setMessage("Your score: $score / ${adapter.itemCount}")
                    .setCancelable(false)
                    .setPositiveButton("OK") { _, _ ->

                        //  Navigate ONLY after OK is pressed
                        val intent = Intent(this@QuizActivity, HomeActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                    }
                    .show()
countDownTimer?.cancel()
                // Disable buttons immediately
                btnSubmit.isEnabled = false
                btnNext.isEnabled = false
                btnBefore.isEnabled = false
            }

        }
    }
//
    // helper to load questions from Room on IO dispatcher
private fun startTimer() {
    countDownTimer = object : CountDownTimer(timeLimitMillis, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            val minutes = (millisUntilFinished / 1000) / 60
            val seconds = (millisUntilFinished / 1000) % 60
            tvtimer.text = String.format(
                "Time Left: %02d:%02d",
                minutes,
                seconds
            )
        }

        override fun onFinish() {
            tvtimer.text = "Time Over!"
            submitQuizAutomatically()
        }
    }.start()
}
    private fun submitQuizAutomatically() {
        val score = adapter.calculateScore()

        lifecycleScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser!!.uid
            val result = QuizResultEntity(
                quizId = quizId,
                score = score,
                totalQuestions = adapter.itemCount,
                userId = userId,

            )
            AppDatabase.getDatabase(this@QuizActivity)
                .quizResultDao()
                .insertResult(result)
        }

        AlertDialog.Builder(this)
            .setTitle("Time Up!")
            .setMessage("Your score: $score / ${adapter.itemCount}")
            .setCancelable(false)
            .setPositiveButton("OK") { _, _ ->
                startActivity(
                    Intent(this, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
                finish()
            }
            .show()

        disableButtons()
    }
    private fun disableButtons(){
        btnSubmit.isEnabled = false
        btnNext.isEnabled = false
        btnBefore.isEnabled = false
    }

    private suspend fun loadQuestionsFromDB(id: Long): List<QuizQuestionEntity> {
        return withContext(Dispatchers.IO) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
            ?: throw IllegalStateException("User not logged in")

            val db = AppDatabase.getDatabase(this@QuizActivity)
            db.quizDao().getQuestionsForQuiz(
                id,
                userId = userId
            )
        }
    }
}