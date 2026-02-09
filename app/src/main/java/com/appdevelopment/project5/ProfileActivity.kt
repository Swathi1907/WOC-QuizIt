package com.appdevelopment.project5

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


class ProfileActivity : AppCompatActivity() {
    private lateinit var profile: ImageView
   // private val PICK_IMAGE = 2007
    private lateinit var tvEmailid: TextView
    private lateinit var etUsername: EditText
   // override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
     //   super.onActivityResult(requestCode, resultCode, data)

       // if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
         //   val uri = data?.data ?: return
           // profile.setImageURI(uri)
           // uploadProfileImage()
        //}
    //}
    //




    override fun onResume() {
        super.onResume()
        loadProfileDta()
    }
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                profile.setImageURI(uri) // preview
uploadProfileImage()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)


         etUsername = findViewById<EditText>(R.id.etUsername)
        tvEmailid = findViewById<TextView>(R.id.tvEmailid)
        val btnUpdate = findViewById<Button>(R.id.update)
        val btnLogout = findViewById<Button>(R.id.btnlogOut)
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        profile = findViewById<ImageView>(R.id.profile)
profile.setOnClickListener {
    pickImage.launch("image/*")
}

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }

                R.id.nav_past -> {
                    startActivity(Intent(this, PastQuizzesActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    // already on ProfileActivity
                    true
                }

                else -> false
            }
        }
        // UPDATE USERNAME
        btnUpdate.setOnClickListener {
            val newUsername = etUsername.text.toString().trim()

            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(
                    mapOf(
                        "username" to newUsername,
                        "email" to user.email
                    ),
                    SetOptions.merge()
                )
                .addOnSuccessListener {
                    Toast.makeText(this, "Username updated", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show()
                }
          //  uploadProfileImage()
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(
                Intent(this, FirstPageActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }


    private fun saveImageUrlToFirestore(url: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .set(
                mapOf("profileImage" to url),
                SetOptions.merge()
            )
    }
    private fun uploadProfileImage() {
        val uri = selectedImageUri ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("profileImages/$uid.jpg")

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    throw task.exception!!
                }
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                saveImageUrlToFirestore(downloadUrl.toString())
            }
            .addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }
    private fun loadProfileDta(){
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val uid = user.uid
        tvEmailid.text = user.email

        // LOAD USERNAME FROM FIRESTORE
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val username = doc.getString("username") ?: ""
                etUsername.setText(username)
                val img = doc.getString("profileImage")
                if (!img.isNullOrEmpty()) {
                    Glide.with(this).load(img).into(profile)
                }
            }
    }
}