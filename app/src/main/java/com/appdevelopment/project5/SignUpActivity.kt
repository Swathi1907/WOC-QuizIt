package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class SignUpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signuppage)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val etusername = findViewById<EditText>(R.id.etUsername)
        val etuniqueId = findViewById<EditText>(R.id.etUniqueId)
        val etemail = findViewById<EditText>(R.id.etEmail)
        val etpassword = findViewById<EditText>(R.id.etPassword)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {
            val uniqueId = etuniqueId.text.toString().trim()
            val username = etusername.text.toString().trim()
            val email = etemail.text.toString().trim()
            val password = etpassword.text.toString().trim()
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || uniqueId.isEmpty()) {
                Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    saveUserProfileAndGo(username, uniqueId, email)
                }
                .addOnFailureListener { e ->

                    if (e is FirebaseAuthUserCollisionException) {

                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                saveUserProfileAndGo(username, uniqueId, email)
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                    }
                }


        }
    }
    private fun saveUserProfileAndGo(
        username: String,
        uniqueId: String,
        email: String
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val userMap = hashMapOf(
            "username" to username,
            "email" to email,
            "uniqueId" to uniqueId
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(userMap, SetOptions.merge()) // overwrite safely
            .addOnSuccessListener {
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
    }

   // override fun onStart() {
     //   super.onStart()
      //  if(
        //    FirebaseAuth.getInstance().currentUser != null
     //   ){
       //     startActivity(Intent(this, HomeActivity::class.java))
         //   finish()
        //}
    //}

}