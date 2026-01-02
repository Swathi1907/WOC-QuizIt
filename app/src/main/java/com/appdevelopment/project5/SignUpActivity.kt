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

class SignUpActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signuppage)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val username = findViewById<EditText>(R.id.etUsername)
        val uniqueId = findViewById<EditText>(R.id.etUniqueId)
 val email = findViewById<EditText>(R.id.etEmail)
        val password = findViewById<EditText>(R.id.etPassword)
        val btnNext = findViewById<Button>(R.id.btnNext)
        btnNext.setOnClickListener {
            val username = username.text.toString().trim()
            val email = email.text.toString().trim()
             val password = password.text.toString().trim()
            if(username.isEmpty() || email.isEmpty()|| password.isEmpty()){
                Toast.makeText(this, "Fill All Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email,password)
                .addOnSuccessListener{
                val uid = auth.currentUser!!.uid
                    val usermap =
                        hashMapOf("username" to username,"email" to email,"uniqueId" to uniqueId)
                    db.collection("users").document(uid)
                        .set(usermap)
                        .addOnSuccessListener{
                            Toast.makeText(this,"Account created", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, ProfileActivity::class.java))
                            finish()
                        }
            }
                .addOnFailureListener { e->
                    if( e is FirebaseAuthUserCollisionException) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                startActivity(Intent(this, ProfileActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Wrong Password", Toast.LENGTH_SHORT).show()
                            }
                    }
                        else
                        {
                            Toast.makeText(this,e.message,Toast.LENGTH_SHORT).show()
                        }


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