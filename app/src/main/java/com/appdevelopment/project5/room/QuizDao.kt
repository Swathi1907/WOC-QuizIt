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
        WHERE quizId = :quizId
        ORDER BY number ASC
    """
    )
    fun getQuestionsForQuiz(
        quizId: Long
    ): List<QuizQuestionEntity>


}