package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appdevelopment.project5.room.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        val btnlogOut = findViewById<Button>(R.id.btnlogOut)
//
        btnlogOut.setOnClickListener {
FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, FirstPageActivity::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
              Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
       }
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_profile -> {

                    true
                }

                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }

                R.id.nav_past -> {
                    startActivity(Intent(this, PastQuizzesActivity::class.java))

                    true
                }



                else -> false
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

                }
               else {
                   tvUsername.text = "user"

                }

            }
            .addOnFailureListener {
                tvUsername.text = "Error loading"

            }
    }
}
