package com.appdevelopment.project5.api


import com.appdevelopment.project5.GeminiConfig
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// -------- Request models --------
data class GeminiRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig?= null

)
data class  Part(
    val text: String
)
data class Content(
    val parts: List<Part>
)
//response models
data class GeminiResponse(
    val candidates: List<Candidate>?
)
data class Candidate(
    val content: Content?
)
//
//

interface GeminiApi {

    @POST("v1beta/models/${GeminiConfig.MODEL}:generateContent")
   suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}