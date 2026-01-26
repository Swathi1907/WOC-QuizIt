package com.appdevelopment.project5
import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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

      //  val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val etusername = findViewById<EditText>(R.id.etUsername)
        val etemail = findViewById<EditText>(R.id.etEmail)

        val etpassword = findViewById<EditText>(R.id.etPassword)
        val btnNext = findViewById<Button>(R.id.btnNext)
        val ivTogglePassword = findViewById<ImageView>(R.id.ivTogglePassword)

        var isPasswordVisible = false

        ivTogglePassword.setOnClickListener{
            if (isPasswordVisible) {
                // Hide password
                etpassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                ivTogglePassword.setImageResource(R.drawable.ic_eyeclosed)
            } else {
                // Show password
                etpassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                ivTogglePassword.setImageResource(R.drawable.ic_eye)
            }

            // Move cursor to end
            etpassword.setSelection(etpassword.text.length)

            isPasswordVisible = !isPasswordVisible
        }
        btnNext.setOnClickListener {


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
                    Toast.makeText(this, "Error in checking Username", Toast.LENGTH_SHORT).show()
                }

        }

    }
    private fun createUser(
        username: String,

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

                }

                else {
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun saveUserProfileAndGo(
        username: String,

        email: String
    ) {
        val user = FirebaseAuth.getInstance().currentUser?: return
        // ?:return means if(user == null) {return } means exit from the function
val uid = user.uid // userid unique string automatically created by firebase authentication never changes
        // always use uid as document id
        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
            displayName = username //firebase doesn't know username but it knows display name
        }
        user.updateProfile(profileUpdates)
            .addOnSuccessListener {
                val userMap = hashMapOf(
                    "username" to username,
                    "email" to email

                )

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(userMap, SetOptions.merge()) // overwrite safely
                    .addOnSuccessListener {
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Profile update failed", Toast.LENGTH_SHORT).show()
                    }
            }
    }



}