package com.appdevelopment.project5.room

import androidx.collection.IntList
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "Quiz_Results")
data class QuizResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val quizId: Long,
    val totalQuestions: Int,
    val score: Int,
    val timestamp: Long = System.currentTimeMillis()

)
