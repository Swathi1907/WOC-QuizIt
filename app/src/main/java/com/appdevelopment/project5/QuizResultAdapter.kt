package com.appdevelopment.project5


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appdevelopment.project5.room.QuizResultEntity
/* the below class connects the quizresults to recycler viewUI
here quiz results is a list of quizresultentity objects coming from roomdatabase
it extends recycler view.adapter, which is required to show lists in android
*/
class QuizResultAdapter(
    private val quizResults: List<QuizResultEntity> ,
    private val Onclick: (Long) -> Unit
) : RecyclerView.Adapter<QuizResultAdapter.QuizResultViewHolder>() {


    // view holder holds the views like textview,buttons etc and onbindview holder connects the adapter and the view holder
    class QuizResultViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
        val tvScore: TextView = itemView.findViewById(R.id.tvScore)
        val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
    }
// the below are three core parts of recycler view adapter
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizResultViewHolder {
        val view = LayoutInflater.from(parent.context) // xml -> view converter
            // layout inflator is a class
            .inflate(R.layout.item_result, parent, false) //converts xml layout file into real view object
        // converts xml to view
        //prepares layout using recycler view as parent and does not attach it like preparing empty boxes and has no data
        //recycler view does not create 100 views for 100 items creates only enough rows to fill screen
        //reuses them while scrolling
        return QuizResultViewHolder(view)
    }
// adapter connects data to UI
    // view holder holds rows views
    // on create view holder creates row
    // on bind view holder fills row with data
    // recycler view shows scrolling list
    override fun onBindViewHolder(holder: QuizResultViewHolder, position: Int) {
        val result = quizResults[position]
// here we bind data to that row
    //recycler view calls it again again while scrolling
    //
        holder.tvScore.text = "Score: ${result.score}"

        holder.tvTotal.text = "Total Questions: ${result.totalQuestions}"

        holder.tvDate.text = "Quiz ID: ${result.quizId}"
        holder.itemView.setOnClickListener {
            Onclick(result.quizId) // when user clicks the row this function is called
            //
        }
    }

    override fun getItemCount(): Int = quizResults.size
}