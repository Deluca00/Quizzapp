package np.com.bimalkafle.quizonline.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.DataSnapshot
import np.com.bimalkafle.quizonline.R

class QuestionActivity : AppCompatActivity() {

    private lateinit var titleEditText: EditText
    private lateinit var subtitleEditText: EditText
    private lateinit var numberEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var addQuestionButton: Button

    private val database = FirebaseDatabase.getInstance()
    private val questionsRef = database.reference.child("question")
    private val lastQuestionIdRef = database.reference.child("lastQuestionId")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        // Initialize views
        titleEditText = findViewById(R.id.questionTitleEditText)
        subtitleEditText = findViewById(R.id.questionSubtitleEditText)
        numberEditText = findViewById(R.id.questionNumberEditText)
        timeEditText = findViewById(R.id.questionTimeEditText)
        addQuestionButton = findViewById(R.id.addQuestionButton)

        addQuestionButton.setOnClickListener {
            addQuestionToFirebase()
        }
    }

    private fun addQuestionToFirebase() {
        val title = titleEditText.text.toString().trim()
        val subtitle = subtitleEditText.text.toString().trim()
        val number = numberEditText.text.toString().trim()
        val time = timeEditText.text.toString().trim()

        // Check if any field is empty
        if (title.isEmpty() || number.isEmpty() || time.isEmpty()) {
            // Show error message or handle the empty fields
            return
        }

        // Use a transaction to increment the lastQuestionId and get the new question ID
        lastQuestionIdRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentId = mutableData.getValue(Int::class.java) ?: 0
                val newId = currentId + 1
                mutableData.value = newId
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                if (committed) {
                    // Get the new question ID
                    val newId = dataSnapshot?.getValue(Int::class.java) ?: return

                    // Create a new question object with the new ID
                    val question = HashMap<String, Any>()
                    val user = FirebaseAuth.getInstance().currentUser
                    question["id"] = user!!.uid
                    question["title"] = title
                    question["subtitle"] = subtitle
                    question["number"] = number.toInt()
                    question["time"] = time.toString()

                    // Save the question to Firebase
                    questionsRef.child(newId.toString()).setValue(question)
                        .addOnSuccessListener {
                            val intent = Intent(this@QuestionActivity, AddnumberquestionActivity::class.java)
                            intent.putExtra("QUESTION_ID", newId.toString())
                            intent.putExtra("NUMBER_QUESTION", number.toInt())
                            startActivity(intent)
                        }
                        .addOnFailureListener { e ->
                            // Error adding question to Firebase
                            // Handle the error, show an error message, or log the exception
                        }
                }
            }
        })
    }

}