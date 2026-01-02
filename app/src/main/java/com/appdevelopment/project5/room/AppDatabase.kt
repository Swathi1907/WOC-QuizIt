package com.appdevelopment.project5.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QuizQuestionEntity::class, QuizResultEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
        abstract fun quizDao(): QuizDao
        abstract fun quizResultDao(): QuizResultDao

    companion object {
                @Volatile private var INSTANCE: AppDatabase? = null

                fun getDatabase(context: Context): AppDatabase {
                        return INSTANCE ?: synchronized(this) {
                                val inst = Room.databaseBuilder(context.applicationContext,
                                        AppDatabase::class.java, "quiz_db"
                                )
                                        .fallbackToDestructiveMigration()
                                        .build()
                                INSTANCE = inst
                                inst
                        }
                }
        }

}