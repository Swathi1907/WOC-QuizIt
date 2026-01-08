package com.appdevelopment.project5
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.QuizQuestionEntity
import com.google.android.play.integrity.internal.q
class QuestionPagerAdapter(
    private val questions: List<QuizQuestionEntity>,
    private val context: Context
) : RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder>() {

    inner class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNumber: TextView = itemView.findViewById(R.id.tvQuestionNumber)
        val tvQuestion: TextView = itemView.findViewById(R.id.tvQuestion)

        val rgOptions: RadioGroup = itemView.findViewById(R.id.optionsGroup)
        val optA: RadioButton = itemView.findViewById(R.id.optA)
        val optB: RadioButton = itemView.findViewById(R.id.optB)
        val optC: RadioButton = itemView.findViewById(R.id.optC)
        val optD: RadioButton = itemView.findViewById(R.id.optD)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_question, parent, false)
        return QuestionViewHolder(view)
    }

    override fun getItemCount(): Int = questions.size

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val q = questions[position]

        holder.tvNumber.text = "Q${q.number}"
        holder.tvQuestion.text = q.question

        holder.optA.text = "A) ${q.optionA}"
        holder.optB.text = "B) ${q.optionB}"
        holder.optC.text = "C) ${q.optionC}"
        holder.optD.text = "D) ${q.optionD}"

        holder.rgOptions.setOnCheckedChangeListener(null)

        when (q.selectedOption) {
            "A" -> holder.optA.isChecked = true
            "B" -> holder.optB.isChecked = true
            "C" -> holder.optC.isChecked = true
            "D" -> holder.optD.isChecked = true
            else -> holder.rgOptions.clearCheck()
        }

        holder.rgOptions.setOnCheckedChangeListener { _, checkedId ->
            q.selectedOption = when (checkedId) {
                holder.optA.id -> "A"
                holder.optB.id -> "B"
                holder.optC.id -> "C"
                holder.optD.id -> "D"
                else -> null
            }

            Log.d("ANSWER_DEBUG", "Q${q.number} selected=${q.selectedOption}")
        }
    }

    // MUST BE HERE (CLASS LEVEL)
    fun calculateScore(): Int {
        var score = 0

        questions.forEach { q ->
            Log.d(
                "SCORE_DEBUG",
                "Q${q.number} selected=${q.selectedOption} correct=${q.correctAnswer}"
            )

            if (
                q.selectedOption != null &&
                q.selectedOption.equals(q.correctAnswer, ignoreCase = true)
            ) {
                score++
            }
        }
        return score
    }
}