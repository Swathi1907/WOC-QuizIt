package com.appdevelopment.project5.firestore

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.FirestoreQuizResult
import com.appdevelopment.project5.R
import com.google.firebase.firestore.local.LocalDocumentsResult
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirestoreQuizAdapter(
    private val quizzes: List<FirestoreQuizResult>,
    private val onClick: (Long) -> Unit
) : RecyclerView.Adapter<FirestoreQuizAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvScore: TextView = view.findViewById(R.id.tvScore)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
val tvTotal : TextView = view.findViewById(R.id.tvTotal)

        init {
            view.setOnClickListener {
                onClick(quizzes[adapterPosition].quizId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return ViewHolder(view)

    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val quiz = quizzes[position]
        holder.tvScore.text =
            "${quiz.score} / ${quiz.totalQuestions}"
holder.tvTotal.text = "Toatal Questions: ${quiz.totalQuestions}"
            val sdf = SimpleDateFormat("dd MMM yyyy",
        Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(quiz.timestamp))
    }

    override fun getItemCount() = quizzes.size
}