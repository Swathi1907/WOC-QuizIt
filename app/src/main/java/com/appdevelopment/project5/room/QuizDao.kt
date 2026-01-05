package com.appdevelopment.project5.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.SkipQueryVerification


@Dao
interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuestions(
        questions: List<QuizQuestionEntity>
    )

    @Query(
        """
        SELECT * FROM QuizQuestionEntity
        WHERE quizId = :quizId AND userId = :userId
        ORDER BY number ASC
    """
    )
    fun getQuestionsForQuiz(
        quizId: Long,
        userId: String
    ): List<QuizQuestionEntity>

@Query("SELECT * FROM Quiz_Results ORDER BY timestamp ASC")
fun getAllResults(): List<QuizResultEntity>

@Query("DELETE FROM QuizQuestionEntity")
fun clearAllQuestions()
}