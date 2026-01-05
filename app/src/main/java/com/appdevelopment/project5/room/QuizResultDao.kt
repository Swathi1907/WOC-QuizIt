
    package com.appdevelopment.project5.room

    import androidx.room.Dao
    import androidx.room.Insert
    import androidx.room.OnConflictStrategy
    import androidx.room.Query

    @Dao
    interface QuizResultDao {

        @Insert(onConflict = OnConflictStrategy.REPLACE)
         fun insertResult(result: QuizResultEntity)

        @Query("SELECT * FROM quiz_results ORDER BY timestamp DESC")
        fun getAllResults(): List<QuizResultEntity>
//
        @Query("DELETE FROM quiz_results")
        fun clearAllResults()
    }