package com.appdevelopment.project5

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val tvEmailid = findViewById<TextView>(R.id.tvEmailid)
val UniqueId = findViewById<TextView>(R.id.tvUniqueId)
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
    }
}
