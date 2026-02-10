package com.appdevelopment.project5

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ProfileFragment : Fragment() {
    private var cameraImageUri: Uri?=null
    private lateinit var profile: ImageView
    private lateinit var tvEmailid: TextView
    private lateinit var etUsername: EditText
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {

                val uri = result.data?.data ?: cameraImageUri

                if (uri != null) {
                    selectedImageUri = uri
                    profile.setImageURI(uri)
                    uploadProfileImage()
                }
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.profile, container, false)
val cam = view.findViewById<ImageView>(R.id.ivCamera)
        profile = view.findViewById(R.id.profile)
        tvEmailid = view.findViewById(R.id.tvEmailid)
        etUsername = view.findViewById(R.id.etUsername)

        val btnUpdate = view.findViewById<Button>(R.id.update)
        val btnLogout = view.findViewById<Button>(R.id.btnlogOut)
        cam.setOnClickListener {
       openImageChooser()
        }

       // profile.setOnClickListener {
         //   pickImage.launch("image/*")
        //}

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
    private fun openImageChooser() {
        // Gallery
        val galleryIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }

        // Camera
        cameraImageUri = createImageUri()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        }

        val chooser = Intent.createChooser(galleryIntent, "Select Profile Image")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(cameraIntent))

        imagePickerLauncher.launch(chooser)
    }
    private fun createImageUri(): Uri {
        val imageFile = File.createTempFile(
            "profile_",
            ".jpg",
            requireContext().cacheDir
        )

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
    }
}