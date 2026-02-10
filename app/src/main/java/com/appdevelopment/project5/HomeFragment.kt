package com.appdevelopment.project5


import androidx.fragment.app.Fragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment: Fragment(R.layout.activity_home) {
    //  lateinit var pvQuizzes: RecyclerView
    //override fun onResume() {
      //  super.onResume()
        //loadUsername()
    //}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Toast.makeText(this, "HomeActivity opened", Toast.LENGTH_SHORT).show()
        super.onViewCreated(view,savedInstanceState)

        //  val btnAttemptedQuizzes = findViewById<Button>(R.id.btnAttemptedQuizzes)
        val tvgreetings = view.findViewById<TextView>(R.id.tvgreetings)
        val btnCreateQuiz = view.findViewById<Button>(R.id.btnCreateQuiz)
        //   pvQuizzes = findViewById<RecyclerView>(R.id.PastQuizzes) //pv=previous
       val profile = view.findViewById<ImageView>(R.id.imageView2)
        val btnPerformances = view.findViewById<Button>(R.id.btnPerformances)
        //  pvQuizzes.layoutManager = LinearLayoutManager(this)
//
        val user = FirebaseAuth.getInstance().currentUser
        val username = user?.displayName ?: "Champ"
        tvgreetings.text = "Hello, $username\uD83D\uDC4B "
        //  profile.setOnClickListener {
        //    val intent = Intent(this, ProfileActivity::class.java)
        //  startActivity(intent)
        //}


        loadUsername(tvgreetings)
loadProfileImage(profile)
        // btnAttemptedQuizzes.setOnClickListener {


        //   loadingQuizResults()
        //}

        btnPerformances.setOnClickListener {

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PerformanceFragment())
                .addToBackStack(null)
                .commit()
        }
        btnCreateQuiz.setOnClickListener {
parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, QuizSetUpFragment())
                .addToBackStack(null)
               .commit()

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
   // private fun openQuizReview(quizId: Long){
    //    val intent = Intent(this,QuizReviewActivity::class.java)
      //  intent.putExtra("QUIZ_ID",quizId)
        //startActivity(intent)
    //}
    private fun loadUsername(tv : TextView) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val username = doc.getString("username") ?: "User"
                    tv.text = "Hello, $username 👋"
                }
            }
    }

    private fun loadProfileImage(profile: ImageView) {
        val user = FirebaseAuth.getInstance().currentUser ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val img = doc.getString("profileImage")
                if (!img.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(img)
                        .circleCrop()
                        .placeholder(R.drawable.boyandgirl)
                        .error(R.drawable.boyandgirl)
                        .into(profile)
                }
            }
    }
}
