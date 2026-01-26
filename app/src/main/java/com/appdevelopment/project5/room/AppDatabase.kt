package com.appdevelopment.project5.room


import android.content.Context //needed to create database . To give access tp app environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [QuizQuestionEntity::class, QuizResultEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() //abstract -> you cannot create object directly
//
{
        abstract fun quizDao(): QuizDao // returns QuizDao , used to access quizquestions table
        abstract fun quizResultDao(): QuizResultDao // used to access quizresult table
// each entity is one table so there are two tables.
    companion object { //used to create only one database instance
                @Volatile private var INSTANCE: AppDatabase? = null //instance -> holds single database object
    // @volatile -> make sure all threads see the same updated value
    //AppDatabase? -> can be null initially
    // =null -> database not created yet

                fun getDatabase(context: Context): AppDatabase {
                        return INSTANCE ?: synchronized(this) { // synchronised prevents duplicate work by locking the code so only one thread runs it at once
                              //with synchronised one thread creates the database others wait


                            //This code creates database once , saves it, and returns it.
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