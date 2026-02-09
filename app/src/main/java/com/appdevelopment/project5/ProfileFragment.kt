package com.appdevelopment.project5

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage

class ProfileFragment : Fragment() {

    private lateinit var profile: ImageView
    private lateinit var tvEmailid: TextView
    private lateinit var etUsername: EditText
    private var selectedImageUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                profile.setImageURI(uri)
                uploadProfileImage()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.profile, container, false)

        profile = view.findViewById(R.id.profile)
        tvEmailid = view.findViewById(R.id.tvEmailid)
        etUsername = view.findViewById(R.id.etUsername)

        val btnUpdate = view.findViewById<Button>(R.id.update)
        val btnLogout = view.findViewById<Button>(R.id.btnlogOut)

        profile.setOnClickListener {
            pickImage.launch("image/*")
        }

        btnUpdate.setOnClickListener {
            updateUsername()
        }

        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            requireActivity().finish()
        }

        loadProfileData()
        return view
    }

    private fun updateUsername() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val newUsername = etUsername.text.toString().trim()

        if (newUsername.isEmpty()) {
            Toast.makeText(requireContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .set(
                mapOf(
                    "username" to newUsername,
                    "email" to user.email
                ),
                SetOptions.merge()
            )
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Username updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Update failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImage() {
        val uri = selectedImageUri ?: return
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val storageRef = FirebaseStorage.getInstance()
            .reference
            .child("profileImages/$uid.jpg")

        storageRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                storageRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(
                        mapOf("profileImage" to downloadUrl.toString()),
                        SetOptions.merge()
                    )
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadProfileData() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        tvEmailid.text = user.email

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                etUsername.setText(doc.getString("username") ?: "")
                val img = doc.getString("profileImage")
                if (!img.isNullOrEmpty()) {
                    Glide.with(this).load(img).into(profile)
                }
            }
    }
}