package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appdevelopment.project5.room.AppDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvEmailid = findViewById<TextView>(R.id.tvEmailid)
val UniqueId = findViewById<TextView>(R.id.tvUniqueId)
        val btnSignOut = findViewById<Button>(R.id.btnSignOut)
//
        btnSignOut.setOnClickListener {

            lifecycleScope.launch {

                // 1️⃣ Clear Room database
                withContext(Dispatchers.IO) {
                    val db = AppDatabase.getDatabase(this@ProfileActivity)
                    db.quizDao().clearAllQuestions()
                    db.quizResultDao().clearAllResults()
                }

                // 2️⃣ Firebase sign out
                FirebaseAuth.getInstance().signOut()

                // 3️⃣ Navigate to first page
                val intent = Intent(this@ProfileActivity, FirstPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
       // val uid = FirebaseAuth.getInstance().currentUser!!.uid
val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        if(user == null){
            finish()
            return
        }
        val uid = user.uid
        tvEmailid.text = user.email

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc->
                if(doc.exists()){
                    tvUsername.text = doc.getString("username")
                    UniqueId.text = doc.getString("uniqueId")
                }
               else {
                   tvUsername.text = "user"
                    UniqueId.text = "Not Set"
                }

            }
            .addOnFailureListener {
                tvUsername.text = "Error loading"
                UniqueId.text = ""
            }
    }
}
