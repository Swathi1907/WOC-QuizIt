package com.appdevelopment.project5.utils

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Extension function to extract text from a Bitmap using ML Kit OCR
 */
suspend fun Bitmap.extractText(): String =
    withContext(Dispatchers.Default) {
        try {
            val image = InputImage.fromBitmap(this@extractText, 0)
//
            val recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

            val result = recognizer.process(image).await()

            result.text.trim().ifEmpty {
                "No text detected"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Text recognition failed"
        }
    }