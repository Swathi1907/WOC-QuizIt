package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {
  //  lateinit var pvQuizzes: RecyclerView
  override fun onResume() {
      super.onResume()
      loadUsername()
  }
    override fun onCreate(savedInstanceState: Bundle?) {
        // Toast.makeText(this, "HomeActivity opened", Toast.LENGTH_SHORT).show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
      //  val btnAttemptedQuizzes = findViewById<Button>(R.id.btnAttemptedQuizzes)
        val tvgreetings = findViewById<TextView>(R.id.tvgreetings)
        val btnCreateQuiz = findViewById<Button>(R.id.btnCreateQuiz)
     //   pvQuizzes = findViewById<RecyclerView>(R.id.PastQuizzes) //pv=previous
        val profile = findViewById<ImageView>(R.id.imageView2)
        val btnPerformances = findViewById<Button>(R.id.btnPerformances)
      //  pvQuizzes.layoutManager = LinearLayoutManager(this)
//
        val user = FirebaseAuth.getInstance().currentUser
        val username = user?.displayName ?: "Champ"
        tvgreetings.text = "Hello, $username\uD83D\uDC4B "
      //  profile.setOnClickListener {
        //    val intent = Intent(this, ProfileActivity::class.java)
          //  startActivity(intent)
        //}
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    true
                }

                R.id.nav_past -> {
                    startActivity(Intent(this, PastQuizzesActivity::class.java))

                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }
       // btnAttemptedQuizzes.setOnClickListener {


         //   loadingQuizResults()
        //}
        btnPerformances.setOnClickListener {

            startActivity(Intent(this, PerformanceActivity::class.java))
        }
        btnCreateQuiz.setOnClickListener {
//
            val intent = Intent(this, QuizSetUpActivity::class.java)
            startActivity(intent)
        }
    }

    /* private fun loadingQuizResults() {
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
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("pastQuizzes")
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener { snapshot ->
                    for (doc in snapshot.documents) {
                        val score = doc.getLong("score")
                        val total = doc.getLong("totalQuestions")
                        val time = doc.getLong("timestamp")

                        Log.d("QUIZ_HISTORY", "$score / $total at $time")
                    }
                }
        } */
    private fun openQuizReview(quizId: Long){
        val intent = Intent(this,QuizReviewActivity::class.java)
        intent.putExtra("QUIZ_ID",quizId)
        startActivity(intent)
    }
    private fun loadUsername() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: "User"
                    findViewById<TextView>(R.id.tvgreetings).text =
                        "Hello, $username 👋"
                }
            }
    }
    }
