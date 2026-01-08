package com.appdevelopment.project5

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.cosh

class QuizSetUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.quizsetup)
        val count = findViewById<EditText>(R.id.etQuestionCount)
        val difficulty = findViewById<Spinner>(R.id.spDifficulty)
        val btnNext2 = findViewById<Button>(R.id.btnNext2)
val ettimer = findViewById<EditText>(R.id.ettimer)

        btnNext2.setOnClickListener {
            val selectedtime = ettimer.text.toString().trim().toIntOrNull()
            val selectedCount = count.text.toString().trim().toIntOrNull()
            val selectedDifficulty = difficulty.selectedItem.toString()
            if( selectedDifficulty.isEmpty() || selectedtime == null || selectedCount==null ){
                Toast.makeText(this,"Fill all the Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(selectedtime == null || selectedtime<=0){
                Toast.makeText(this,"Enter a valid time ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(selectedCount == null ||selectedCount<=0){
                Toast.makeText(this,"Enter a valid question count ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("count", selectedCount)
            intent.putExtra("difficulty", selectedDifficulty)
               intent.putExtra("TIME_LIMIT",selectedtime)
            startActivity(intent)
        }
    }
}