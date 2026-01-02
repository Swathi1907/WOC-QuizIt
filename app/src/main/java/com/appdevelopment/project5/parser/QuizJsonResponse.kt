package com.appdevelopment.project5.parser



data class QuizJsonResponse(
    val questions: List<QuizJsonQuestion>
)

data class QuizJsonQuestion(
    val number: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)