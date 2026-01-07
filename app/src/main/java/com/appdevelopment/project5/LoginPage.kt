package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginPage : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.loginpage)

      etUsername = findViewById(R.id.etusername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnNext)

        btnLogin.setOnClickListener {
            loginUser()
        }
    }
//

    private fun loginUser() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // 🔍 Step 1: Find email using Unique ID
        db.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { query ->
                if (query.isEmpty) {
                    Toast.makeText(this, "No account found. Please create an account first.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val email = query.documents[0].getString("email")!!

                // 🔐 Step 2: Login using Firebase Auth
                auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        startActivity(
                            Intent(this, HomeActivity::class.java)
                        )
                        finish()
                    }
                    .addOnFailureListener {
                        Log.e("AUTH_ERROR",it.message ?: "error")
                        Toast.makeText(this, "Wrong Credentials", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
    }
}