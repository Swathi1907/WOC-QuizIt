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
import com.google.firebase.firestore.FirebaseFirestore
import com.appdevelopment.project5.firestore.FirestoreQuizAdapter
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

   /* private fun loadPastQuizzes() {

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
    } */
   private fun loadPastQuizzes() {

       val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

       FirebaseFirestore.getInstance()
           .collection("users")
           .document(uid)
           .collection("pastQuizzes")
           .orderBy("timestamp")
           .get()
           .addOnSuccessListener { snapshot ->

               if (snapshot.isEmpty) {
                   Toast.makeText(
                       this,
                       "No past quizzes found",
                       Toast.LENGTH_SHORT
                   ).show()
                   return@addOnSuccessListener
               }

               val results = snapshot.documents.map { doc ->
                   FirestoreQuizResult(
                       quizId = doc.getLong("quizId") ?: 0L,
                       score = doc.getLong("score")?.toInt() ?: 0,
                       totalQuestions = doc.getLong("totalQuestions")?.toInt() ?: 0,
                       difficulty = doc.getString("difficulty") ?: "",
                       timestamp = doc.getLong("timestamp") ?: 0L
                   )
               }

               recyclerView.adapter =
                   FirestoreQuizAdapter(results) { quizId ->
                       openQuizReview(quizId)
                   }
           }
           .addOnFailureListener {
               Toast.makeText(
                   this,
                   "Failed to load past quizzes",
                   Toast.LENGTH_SHORT
               ).show()
           }
   }

    private fun openQuizReview(quizId: Long) {
        val intent = Intent(this, QuizReviewActivity::class.java)
        intent.putExtra("QUIZ_ID", quizId)
        startActivity(intent)
    }
}

