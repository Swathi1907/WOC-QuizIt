package com.appdevelopment.project5

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.firestore.FirestoreQuestion
import com.appdevelopment.project5.firestore.FirestoreReviewAdapter
import com.appdevelopment.project5.room.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class QuizReviewActivity : AppCompatActivity() {
private lateinit var rv: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_review)
 rv = findViewById<RecyclerView>(R.id.rvReview)
        rv.layoutManager = LinearLayoutManager(this)
        val quizId = intent.getLongExtra("QUIZ_ID", -1)
        loadQuestionsFromFirestore(quizId)

       /* lifecycleScope.launch(Dispatchers.IO) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
                ?: run {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@QuizReviewActivity, "User not logged in", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    return@launch
                }
            val questions =
                AppDatabase.getDatabase(this@QuizReviewActivity)
                    .quizDao()
                    .getQuestionsForQuiz(quizId = quizId, userId = userId)
            Log.d("REVIEW", "quizId = $quizId")
            Log.d("REVIEW", "questions size = ${questions.size}")
            withContext(Dispatchers.Main) {
                rv.adapter =
                    ReviewAdapter(questions)
            }
        } */
    }
    private fun loadQuestionsFromFirestore(quizId: Long) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .collection("pastQuizzes")
            .document(quizId.toString())
            .collection("questions")
            .orderBy(FieldPath.documentId())
            .get()
            .addOnSuccessListener { snapshot ->

                if (snapshot.isEmpty) {
                    Toast.makeText(this, "No questions found", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val questions = snapshot.documents.map { doc ->
                    FirestoreQuestion(
                        question = doc.getString("question") ?: "",
                        options = doc.get("options") as List<String>,
                        correctAnswer = doc.getString("correctAnswer") ?: "",
                        explanation = doc.getString("explanation") ?: "",
                        selectedOption = doc.getString("selectedOption")
                    )
                }

               rv.adapter = FirestoreReviewAdapter(questions)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load quiz", Toast.LENGTH_SHORT).show()
            }
    }
}
//