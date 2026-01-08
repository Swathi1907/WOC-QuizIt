package com.appdevelopment.project5

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.appdevelopment.project5.api.ApiClient
import com.appdevelopment.project5.api.GeminiRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.io.File
import com.appdevelopment.project5.api.Content
import com.appdevelopment.project5.api.Part
import com.appdevelopment.project5.api.GenerationConfig
import com.appdevelopment.project5.parser.JsonQuizParser
import com.appdevelopment.project5.parser.RegexQuizParser
import com.appdevelopment.project5.room.AppDatabase
import com.appdevelopment.project5.room.QuizQuestionEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.coroutines.tasks.await


class MainActivity : AppCompatActivity() {

    private val PDFPICKCODE = 2001
    private lateinit var btnGenerateAI: Button
    private lateinit var tvOutput: TextView
    private var selectedCount =0

    private var  timeinminutes= 0

    private var selectedDifficulty = ""
private var extractedText: String=""

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
      //  Toast.makeText(this,"MainActivity opened",Toast.LENGTH_SHORT).show()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
Log.d("KEY_TEST","Gemini key = ${GeminiConfig.API_KEY}")

        val btnPickPdf = findViewById<Button>(R.id.btnPickPdf)
        val btnFetchUrl = findViewById<Button>(R.id.btnFetchUrl)
        val etUrl = findViewById<EditText>(R.id.etUrl)
        val btnExtractedText = findViewById<Button>(R.id.btnExtractedText)
        tvOutput = findViewById(R.id.tvOutput)
         btnGenerateAI = findViewById(R.id.btnGenerateAI)
selectedCount = intent.getIntExtra("count", -1)
        selectedDifficulty = intent.getStringExtra("difficulty")?:"medium"
timeinminutes = intent.getIntExtra("TIME_LIMIT",5)
        if (selectedCount <= 0) {
            Toast.makeText(this, "Invalid question count", Toast.LENGTH_SHORT).show()
            return
        }
        btnGenerateAI.setOnClickListener {
            Log.d("QuizSetUpActivity","count=$selectedCount , difficulty=$selectedDifficulty")
         val content= extractedText.trim()
            if(content.isBlank()){
                Toast.makeText(this@MainActivity,"Content Is Empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
val safeContent = content.take(2000)
            //Temporary: Ask for 3 questions
           val prompt = PromptBuilder.buildPrompt(content = safeContent, count = selectedCount, difficulty = selectedDifficulty)
            Toast.makeText(this,"Please wait while we process your request...",Toast.LENGTH_LONG).show()
            callGeminiDirect(prompt)
        }

        btnPickPdf.setOnClickListener {
            pickPdf()
        }
btnExtractedText.setOnClickListener {
    if(extractedText.isBlank()) {
        Toast.makeText(this@MainActivity, "No Content Extracted Yet", Toast.LENGTH_SHORT).show()
        return@setOnClickListener

    }
    tvOutput.text = extractedText
}
        btnFetchUrl.setOnClickListener {
            val url = etUrl.text.toString().trim()
            if (url.isEmpty()) {
                Toast.makeText(this, "Enter a URL", Toast.LENGTH_SHORT).show()
            } else {
                tvOutput.text = "Fetching..."
                fetchUrlText(url, tvOutput)
            }
        }
     //   backbutton.setOnClickListener {
      //      Intent(this@MainActivity, HomeActivity::class.java)
      //  }
    }

    private fun pickPdf() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, PDFPICKCODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val tvOutput = findViewById<TextView>(R.id.tvOutput)

        if (requestCode == PDFPICKCODE && resultCode == Activity.RESULT_OK) {
            val uri: Uri? = data?.data
            if (uri != null) {
                // grant persistable permission for Drive/Cloud URIs
                try {
                    val takeFlags = data.flags and
                            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (_: Exception) { /* ignore */
                }

                tvOutput.text = "Extracting content in the PDF"
                // run extraction in background
                lifecycleScope.launch(Dispatchers.IO) {

                    val result = try {
                        renderLimitedPagesAndRecognize(uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        "Error: ${e.message}"
                    }
                    withContext(Dispatchers.Main) {
                        extractedText = result
                        Toast.makeText(this@MainActivity,"PDF Text Extracted", Toast.LENGTH_SHORT).show()
tvOutput.text = "Extracted text will appear here"
                    }
                }
            } else {
                tvOutput.text = "No PDF selected"
            }
        }
    }

    // Copy Uri content to a temporary file (helps with providers that don't expose descriptor)
    private fun copyUriToTempFile(uri: Uri): File {
        val temp = File.createTempFile("picked_pdf_", ".pdf", cacheDir)
        contentResolver.openInputStream(uri)?.use { input ->
            temp.outputStream().use { out -> input.copyTo(out) }
        }
        return temp
    }

    // Render first page and run ML Kit OCR (suspending)
    private suspend fun renderLimitedPagesAndRecognize(uri: Uri): String {
        val tmp = copyUriToTempFile(uri)
        var pfd: ParcelFileDescriptor? = null
        var renderer: PdfRenderer? = null

        val finalText = StringBuilder()
        val MAX_PAGES = 6   // /////////// max pages will get extracted

        try {
            pfd = ParcelFileDescriptor.open(tmp, ParcelFileDescriptor.MODE_READ_ONLY)
            renderer = PdfRenderer(pfd)

            val totalPages = renderer.pageCount
            if (totalPages == 0) return "PDF has no pages."

            val pagesToProcess = minOf(totalPages, MAX_PAGES)

            Log.d("PDF_OCR", "Total pages in PDF = $totalPages")
            Log.d("PDF_OCR", "Extracting $pagesToProcess pages")

            val recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

            for (i in 0 until pagesToProcess) {
                Log.d("PDF_OCR", "Processing page ${i + 1}/$pagesToProcess")

                val page = renderer.openPage(i)

                val width = page.width * 2
                val height = page.height * 2
                val bitmap = Bitmap.createBitmap(
                    width,
                    height,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.eraseColor(Color.WHITE)

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                page.close()

                val pageText = runTextRecognition(recognizer, bitmap)
                bitmap.recycle()

                if (pageText.isNotBlank()) {
                    finalText.append("\n\n--- Page ${i + 1} ---\n")
                    finalText.append(pageText)
                }

                Log.d("PDF_OCR", "Finished page ${i + 1}")
            }

            Log.d("PDF_OCR", "OCR completed. Pages extracted = $pagesToProcess")

            return finalText
                .toString()
                .take(8_000)
                .ifBlank { "No text found in extracted pages." }

        } finally {
            try { renderer?.close() } catch (_: Exception) {}
            try { pfd?.close() } catch (_: Exception) {}
            try { tmp.delete() } catch (_: Exception) {}
        }
    }

    // suspend wrapper to await ML Kit Task


    private suspend fun runTextRecognition(
        recognizer: TextRecognizer,
        bitmap: Bitmap
    ): String = withContext(Dispatchers.Default) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()
            result.text.trim().ifEmpty { "No text detected" }
        } catch (e: Exception) {
            "OCR failed: ${e.message}"
        }
    }
    //ai output-> parsed using regex;parsed questions -> saved into room;quiz screen -> automatically opened!

    // ai output-> parsed using regex; parsed questions -> saved into room; quiz screen -> automatically opened!
    private fun callGeminiDirect(prompt1: String) {



        lifecycleScope.launch {
            try {
                val userId = FirebaseAuth.getInstance().currentUser?.uid
                    ?: run {
                       Toast.makeText(this@MainActivity,"user",Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                Log.d("GEMINI_CALL","Calling Gemini API....")
                val request = GeminiRequest(
                    contents = listOf(
                        Content(
                            parts = listOf(
                                Part(text = prompt1)
                            )
                        )
                    ),
                    generationConfig = GenerationConfig(
                        maxOutputTokens = 3000

                    )
                )

                val response = ApiClient.geminiApi.generateContent(
                    apiKey = GeminiConfig.API_KEY,
                    request = request
                )
Log.d("GEMINI_HTTP","Code = ${response.code()}")
                if (!response.isSuccessful) {
                    val err = response.errorBody()?.string()
                    Log.e("GEMINI_RAW", err?:"No error body")
                    tvOutput.text = "HTTP ${response.code()} ${response.message()}"
                    return@launch
                }
Log.d("GEMINI_RAW",response.body().toString())
                val rawText = response.body()
                    ?.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text ?: ""

                Log.d("RAW_GEMINI", rawText)
                Log.d("RAW_LEN", "Length = ${rawText.length}")
                if (!rawText.trim().endsWith("}")) {
                    tvOutput.text = "AI response incomplete. Try again."
                    Log.e("GEMINI_INCOMPLETE", rawText)
                    return@launch
                }
// Parse JSON
                val parsed =
                    JsonQuizParser.parse(rawText)

                Log.d("PARSED_SIZE", "size = ${parsed.size}")

                if (parsed.isEmpty()) {
                    tvOutput.text = "Quiz generation failed"
                    return@launch
                }

//  Save to Room
                val quizId = System.currentTimeMillis()
//

               val entities = parsed.mapIndexed { index, q ->
                   val correctOption = when(q.correctAnswer.trim()) {

                       q.options[0].trim() -> "A"
                       q.options[1].trim() -> "B"
                       q.options[2].trim() -> "C"
                       q.options[3].trim() -> "D"

                       else -> ""
                   }
                    QuizQuestionEntity(
                        userId = userId,
                        quizId = quizId,
                        number = index + 1,
                        question = q.question,
                        optionA = q.options[0],
                        optionB = q.options[1],
                        optionC = q.options[2],
                        optionD = q.options[3],
                        correctAnswer = correctOption,
                        explanation = q.explanation,
                       selectedOption = null
                    )
               }
                Log.d("ROOM_DEBUG", "Questions to insert = ${entities.size}")
                entities.forEach {
                    Log.d("ROOM_DEBUG", "Q number=${it.number}, quizId=${it.quizId}")
                }

                withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(this@MainActivity)
                        .quizDao()
                        .insertQuestions(entities)
                }

//  Open QuizActivity
                startActivity(
                    Intent(this@MainActivity, QuizActivity::class.java)
                        .putExtra("QUIZ_ID", quizId)
                        .putExtra("TIME_LIMIT",timeinminutes)
                )



            } catch (e: Exception) {
                Log.e("GEMINI_EXCEPTION",e.toString())
                tvOutput.text = "Error: ${e.message}"
            }
        }
    }



                // Simple JSoup fetch on background thread
                private fun fetchUrlText(url: String, tvOutput: TextView) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            val doc = Jsoup.connect(url).userAgent("Mozilla/5.0")
                                .timeout(15_000)
                                .get()
                            val extracted = doc.body().text()

                            val safeText = extracted.take(1200)
                            withContext(Dispatchers.Main) {

                          extractedText = safeText
                                Toast.makeText(this@MainActivity,"URL Text Extracted", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {

                                tvOutput.text = "Error: ${e.message}"
                            }
                        }
                    }
                }
}






