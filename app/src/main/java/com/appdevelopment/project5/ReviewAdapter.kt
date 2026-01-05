package com.appdevelopment.project5

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.QuizQuestionEntity

class ReviewAdapter(
    private val questions: List<QuizQuestionEntity>
) : RecyclerView.Adapter<ReviewAdapter.ReviewVH>() {

    class ReviewVH(view: View) : RecyclerView.ViewHolder(view) {
        val q: TextView = view.findViewById(R.id.tvQuestion)
        val a: TextView = view.findViewById(R.id.tvA)
        val b: TextView = view.findViewById(R.id.tvB)
        val c: TextView = view.findViewById(R.id.tvC)
        val d: TextView = view.findViewById(R.id.tvD)
        val correct: TextView = view.findViewById(R.id.tvCorrect)
        val explanation: TextView = view.findViewById(R.id.tvExplanation)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review_question, parent, false)
        return ReviewVH(v)
    }

    override fun onBindViewHolder(h: ReviewVH, pos: Int) {
        val q = questions[pos]

        h.q.text = q.question
        h.a.text = "A) ${q.optionA}"
        h.b.text = "B) ${q.optionB}"
        h.c.text = "C) ${q.optionC}"
        h.d.text = "D) ${q.optionD}"

        h.correct.text = "Correct Answer: ${q.correctAnswer}"
        h.explanation.text = "Explanation: ${q.explanation}"
    }

    override fun getItemCount() = questions.size
}