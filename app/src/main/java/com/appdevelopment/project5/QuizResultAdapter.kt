package com.appdevelopment.project5


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.QuizResultEntity
/* the below class connects the quizresults to recycler viewUI
here quiz results is a list of quizresultentity objects coming from roomdatabase
it extends recycler view.adapter, which is required to show lists efficiently in android
*/
class QuizResultAdapter(
    private val quizResults: List<QuizResultEntity> ,
    private val Onclick: (Long) -> Unit
) : RecyclerView.Adapter<QuizResultAdapter.QuizResultViewHolder>() {


    // view holder holds the views like textview,buttons etc and onbindview holder connects the adapter and the view holder
    class QuizResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizResultViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_result, parent, false)
        return QuizResultViewHolder(view)
    }
//
    override fun onBindViewHolder(holder: QuizResultViewHolder, position: Int) {
        val result = quizResults[position]

        holder.tvScore.text = "Score: ${result.score}"
        holder.tvTotal.text = "Total Questions: ${result.totalQuestions}"
        holder.tvDate.text = "Quiz ID: ${result.quizId}"
        holder.itemView.setOnClickListener {
            Onclick(result.quizId)
        }
    }

    override fun getItemCount(): Int = quizResults.size
}