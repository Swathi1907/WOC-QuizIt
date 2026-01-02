package com.appdevelopment.project5



import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.QuizQuestionEntity

class QuestionPagerAdapter(
    private val questions: List<QuizQuestionEntity>,
    private val context: Context
) : RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder>() {

    // Stores selected answers for each position: position -> "A"/"B"/"C"/"D"
    private val selectedAnswers = mutableMapOf<Int, String>()

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.tvQuestionNumber)
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)
        val tvExplanation: TextView = itemView.findViewById(R.id.tvExplanation)

        val rgOptions: RadioGroup = itemView.findViewById(R.id.optionsGroup)
        val optA: RadioButton = itemView.findViewById(R.id.optA)
        val optB: RadioButton = itemView.findViewById(R.id.optB)
        val optC: RadioButton = itemView.findViewById(R.id.optC)
        val optD: RadioButton = itemView.findViewById(R.id.optD)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val q = questions[position]

        // Fill UI
        holder.tvNumber.text = "${q.number}."
        holder.tvQuestion.text = q.question
        holder.optA.text = "A) ${q.optionA}"
        holder.optB.text = "B) ${q.optionB}"
        holder.optC.text = "C) ${q.optionC}"
        holder.optD.text = "D) ${q.optionD}"

        // Hide explanation when quiz is going on
        holder.tvExplanation.visibility = View.GONE
        holder.tvExplanation.text = q.explanation

        // Clear previous listener & selection to avoid recycled-listener bugs
        holder.rgOptions.setOnCheckedChangeListener(null)
        holder.rgOptions.clearCheck()

        // Restore previous selection if any
        when (selectedAnswers[position]) {
            "A" -> holder.optA.isChecked = true
            "B" -> holder.optB.isChecked = true
            "C" -> holder.optC.isChecked = true
            "D" -> holder.optD.isChecked = true
        }

        // Listen for new user selection and save it
        holder.rgOptions.setOnCheckedChangeListener { _, checkedId ->
            val selected = when (checkedId) {
                holder.optA.id -> "A"
                holder.optB.id -> "B"
                holder.optC.id -> "C"
                holder.optD.id -> "D"
                else -> null
            }
            if (selected != null) {
                selectedAnswers[position] = selected
            }
        }
    }

    /*Calculate score by comparing stored selections with the correctAnswer field
      in QuizQuestionEntity. Returns number of correct answers.
     */
    fun calculateScore(): Int {
        var score = 0
        for (i in questions.indices) {
            val selected = selectedAnswers[i]
            val correct = questions[i].correctAnswer
            if (!selected.isNullOrBlank() && selected.equals(correct, ignoreCase = true)) {
                score++
            }
        }
        return score
    }
}