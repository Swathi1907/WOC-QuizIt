package com.appdevelopment.project5.firestore

data class FirestoreQuestion(
    val question: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: String = "",
    val explanation: String = "",
    val selectedOption: String? = ""
)