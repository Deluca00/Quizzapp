package np.com.bimalkafle.quizonline.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import np.com.bimalkafle.quizonline.R


class AddnumberquestionActivity : AppCompatActivity() {

    private lateinit var tw1TextView: TextView
    private lateinit var questionEditText: EditText
    private lateinit var ops1EditText: EditText
    private lateinit var ops2EditText: EditText
    private lateinit var ops3EditText: EditText
    private lateinit var ops4EditText: EditText
    private lateinit var correctEditText: EditText
    private lateinit var addQsButton: Button

    private val database = FirebaseDatabase.getInstance()
    private val questionsRef = database.reference.child("question")

    private var questionsAdded = 0
    private var numberQuestion = 0
    private var questionId: String? = null
    private var questionNumber = 1

    // Map to store questions with their index as key
    private val questionList = mutableMapOf<String, Map<String, Any>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnumberquestion)

        // Initialize views
        tw1TextView = findViewById(R.id.tw1)
        questionEditText = findViewById(R.id.question)
        ops1EditText = findViewById(R.id.ops1)
        ops2EditText = findViewById(R.id.ops2)
        ops3EditText = findViewById(R.id.ops3)
        ops4EditText = findViewById(R.id.ops4)
        correctEditText = findViewById(R.id.correct)
        addQsButton = findViewById(R.id.addQsButton)

        // Get data from intent
        questionId = intent.getStringExtra("QUESTION_ID")
        numberQuestion = intent.getIntExtra("NUMBER_QUESTION", 1)

        updateQuestionNumber()

        addQsButton.setOnClickListener {
            if (questionsAdded < numberQuestion) {
                addQuestionToList()
            } else {
                saveQuestionsToFirebase()
            }
        }



    }


    private fun addQuestionToList() {
        val questionText = questionEditText.text.toString().trim()
        val options = listOf(
            ops1EditText.text.toString().trim(),
            ops2EditText.text.toString().trim(),
            ops3EditText.text.toString().trim(),
            ops4EditText.text.toString().trim()
        )
        val correct = correctEditText.text.toString().trim()

        if (questionText.isEmpty() || options.any { it.isEmpty() } || correct.isEmpty()) {
            // Show error message or handle the empty fields
            return
        }

        // Create a new question object
        val question = mapOf(
            "question" to questionText,
            "options" to options,
            "correct" to correct
        )

        // Add question to the map with its index as the key
        questionList[questionsAdded.toString()] = question
        questionsAdded++
        clearFields()
        questionNumber++
        updateQuestionNumber()

        if (questionsAdded >= numberQuestion) {
            Toast.makeText(this, "All questions added", Toast.LENGTH_SHORT).show()
            saveQuestionsToFirebase()
        } else {
            Toast.makeText(this, "Question added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveQuestionsToFirebase() {
        questionId?.let {
            // Save the map of questions to Firebase
            questionsRef.child(it).child("questionList").setValue(questionList)
                .addOnSuccessListener {
                    Toast.makeText(this, "All questions saved to Firebase", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MainActivity_menu::class.java))
                    finish()


                }

                .addOnFailureListener { e ->

                }
        }
    }

    private fun updateQuestionNumber() {
        tw1TextView.text = "Question $questionNumber"
    }

    private fun clearFields() {
        questionEditText.text.clear()
        ops1EditText.text.clear()
        ops2EditText.text.clear()
        ops3EditText.text.clear()
        ops4EditText.text.clear()
        correctEditText.text.clear()
    }
}
