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
       // val etuniqueId = findViewById<EditText>(R.id.etUniqueId)
        val etemail = findViewById<EditText>(R.id.etEmail)
        val etpassword = findViewById<EditText>(R.id.etPassword)
        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {

         //   val uniqueId = etuniqueId.text.toString().trim()
            val username = etusername.text.toString().trim()
            val email = etemail.text.toString().trim()
            val password = etpassword.text.toString().trim()

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() ) {
                Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }
                    // continue signup

                    createUser(username,email,password)
//
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error in checking Unique Id", Toast.LENGTH_SHORT).show()
                }

        }

    }
    private fun saveUserProfileAndGo(
        username: String,
       // uniqueId: String,
        email: String
    ) {
        val user = FirebaseAuth.getInstance().currentUser?: return
val uid = user.uid
        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
            displayName = username
        }
        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                val userMap = hashMapOf(
                    "username" to username,
                    "email" to email,
                 //   "uniqueId" to uniqueId
                )

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(userMap, SetOptions.merge()) // overwrite safely
                    .addOnSuccessListener {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show()
                    }
            }
    }
    private fun createUser(
        username: String,
       // uniqueId: String,
        email: String,
        password: String
    ){
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                saveUserProfileAndGo(username,  email)
            }
            .addOnFailureListener { e ->

                if (e is FirebaseAuthUserCollisionException) {
Toast.makeText(this,"Email already exists",Toast.LENGTH_SHORT).show()
                 //   auth.signInWithEmailAndPassword(email, password)
                   //     .addOnSuccessListener {
                     //       saveUserProfileAndGo(username,  email)
                       // }
                        //.addOnFailureListener {
                          //  Toast.makeText(this, "Wrong password", Toast.LENGTH_SHORT).show()
                        }

                 else {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
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