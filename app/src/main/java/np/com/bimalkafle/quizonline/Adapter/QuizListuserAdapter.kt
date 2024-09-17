package np.com.bimalkafle.quizonline.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import np.com.bimalkafle.quizonline.Class.QuizModel
import np.com.bimalkafle.quizonline.databinding.QuizItemRecyclerUserBinding

class QuizListuserAdapter(private val quizModelList: MutableList<QuizModel>) :
    RecyclerView.Adapter<QuizListuserAdapter.MyViewHolder>() {

    class MyViewHolder(private val binding: QuizItemRecyclerUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: QuizModel, onDeleteClick: (QuizModel, Int) -> Unit) {
            binding.apply {
                quizTitleText.text = model.title
                quizSubtitleText.text = model.subtitle
                xoakhoahoc.setOnClickListener {
                    onDeleteClick(model, adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = QuizItemRecyclerUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return quizModelList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(quizModelList[position]) { quizModel, position ->
            deleteQuizFromFirebase(quizModel.id) { success ->
                if (success) {
                    quizModelList.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, quizModelList.size)
                } else {
                    println("Failed to delete quiz")
                }
            }
        }
    }


    private fun deleteQuizFromFirebase(quizId: String, callback: (Boolean) -> Unit) {
        val lastQuestionIdRef = FirebaseDatabase.getInstance().reference.child("lastQuestionId")

        // Lắng nghe sự thay đổi của lastQuestionId
        lastQuestionIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val lastQuestionId = dataSnapshot.getValue(Int::class.java)
                if (lastQuestionId != null) {
                    // Xóa dữ liệu dựa trên lastQuestionId
                    val ref = FirebaseDatabase.getInstance().reference.child("question").child(lastQuestionId.toString())
                    ref.removeValue()
                        .addOnCompleteListener { task ->
                            callback(task.isSuccessful)
                        }
                        .addOnFailureListener { exception ->
                            callback(false)
                        }
                } else {
                    callback(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                callback(false)
            }
        })
    }
}