package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class FirstPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.firstpage)
        val login = findViewById<Button>(R.id.btnLogin)
        val SignUp = findViewById<Button>(R.id.btnSignUp)

       login.setOnClickListener {
//
          startActivity(Intent(this, LoginPage::class.java))
    }
        SignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
    override fun onStart() {
        super.onStart()

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            // User already logged in → go to Home
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
//