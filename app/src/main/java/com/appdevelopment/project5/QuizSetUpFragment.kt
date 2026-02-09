package com.appdevelopment.project5



import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlin.math.cosh

class QuizSetUpFragment :  Fragment(R.layout.quizsetup) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val count = view.findViewById<EditText>(R.id.etQuestionCount)
        val difficulty = view.findViewById<Spinner>(R.id.spDifficulty)
        val btnNext2 = view.findViewById<Button>(R.id.btnNext2)
        val ettimer = view.findViewById<EditText>(R.id.ettimer)
        //  val etquiztitle = findViewById<EditText>(R.id.etquiztitle)


        btnNext2.setOnClickListener {
            //     val quiztitle = etquiztitle.text.toString().trim()
            val selectedtime = ettimer.text.toString().trim().toIntOrNull()
            val selectedCount = count.text.toString().trim().toIntOrNull()
            val selectedDifficulty = difficulty.selectedItem.toString()
            if (selectedDifficulty.isEmpty() || selectedtime == null || selectedCount == null) {
                Toast.makeText(requireContext(), "Fill all the Fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedtime == null || selectedtime <= 0) {
                Toast.makeText(requireContext(), "Enter a valid time ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (selectedCount == null || selectedCount <= 0) {
                Toast.makeText(requireContext(), "Enter a valid question count ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }




                val quizfragment= MainFragment().apply {
                    arguments = Bundle().apply {
                        putInt("count", selectedCount)
                        putString("difficulty", selectedDifficulty)
                        putInt("TIME_LIMIT",selectedtime)
                    }
                }

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, quizfragment)
                    .addToBackStack(null)
                    .commit()

        }

    }
}