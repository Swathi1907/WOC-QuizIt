package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity

class QuizSetUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quizsetup)
        val count = findViewById<EditText>(R.id.etQuestionCount)
        val difficulty = findViewById<Spinner>(R.id.spDifficulty)
        val btnNext2 = findViewById<Button>(R.id.btnNext2)

        btnNext2.setOnClickListener {
            val count = count.text.toString().toInt()
            val difficulty = difficulty.selectedItem.toString()

                intent.putExtra("count", count)
            intent.putExtra("difficulty", difficulty)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}