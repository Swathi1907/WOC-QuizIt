package com.appdevelopment.project5.room


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: String,
    val quizId: Long,
    val number: Int,
    val question: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String,
    val explanation: String,
    val correctOption: String,
    var selectedOption: Int = -1
)
//