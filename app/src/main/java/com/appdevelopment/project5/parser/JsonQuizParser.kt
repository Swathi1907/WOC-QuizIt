package com.appdevelopment.project5.parser

import com.google.gson.Gson

object JsonQuizParser {
//
    fun parse(json: String): List<QuizJsonQuestion> {
        return try {
            val response = Gson().fromJson(json, QuizJsonResponse::class.java)
            response.questions
        } catch (e: Exception) {
            emptyList()
        }
    }
}