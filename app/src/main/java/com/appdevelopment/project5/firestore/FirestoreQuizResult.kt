

package com.appdevelopment.project5

data class FirestoreQuizResult(
    val quizId: Long = 0L,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val difficulty: String = "",
    val timestamp: Long = 0L,
    val userId: String = ""
)