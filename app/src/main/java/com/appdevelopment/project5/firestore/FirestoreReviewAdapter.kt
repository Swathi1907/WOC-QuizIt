package com.appdevelopment.project5.firestore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


import android.view.View
import android.widget.TextView

import com.appdevelopment.project5.FirestoreQuizResult
import com.appdevelopment.project5.R
import com.google.firebase.firestore.local.LocalDocumentsResult
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FirestoreReviewAdapter(
    private val questions: List<FirestoreQuestion>
) : RecyclerView.Adapter<FirestoreReviewAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val qNumber: TextView = v.findViewById(R.id.tvQuestionNumber)
        val q: TextView = v.findViewById(R.id.tvQuestion)
        val a: TextView = v.findViewById(R.id.tvA)
        val b: TextView = v.findViewById(R.id.tvB)
        val c: TextView = v.findViewById(R.id.tvC)
        val d: TextView = v.findViewById(R.id.tvD)
        val correct: TextView = v.findViewById(R.id.tvCorrect)
        val explanation: TextView = v.findViewById(R.id.tvExplanation)
    }

    override fun onCreateViewHolder(p: ViewGroup, v: Int): VH {
        return VH(
            LayoutInflater.from(p.context)
                .inflate(R.layout.item_review_question, p, false)
        )
    }



    override fun onBindViewHolder(h: VH, pos: Int) {
        val q = questions[pos]

        h.qNumber.text = "Question ${pos + 1}"
        h.q.text = q.question
        h.a.text = "A) ${q.options[0]}"
        h.b.text = "B) ${q.options[1]}"
        h.c.text = "C) ${q.options[2]}"
        h.d.text = "D) ${q.options[3]}"
        h.correct.text = "Correct Answer: ${q.correctAnswer}"
        h.explanation.text = "Explanation: ${q.explanation}"
    }

    override fun getItemCount() = questions.size
}