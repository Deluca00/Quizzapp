package np.com.bimalkafle.quizonline.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import np.com.bimalkafle.quizonline.R

class SigupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigup)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val emailEditText: EditText = findViewById(R.id.email_edit_text)
        val userEditText: EditText = findViewById(R.id.user_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password_edit_text)
        val signupButton: Button = findViewById(R.id.signup_btn)
        val signinLinkTextView: TextView = findViewById(R.id.linksignnin)

        signinLinkTextView.setOnClickListener {
            startActivity(Intent(this, SiginActivity::class.java))
        }

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val username = userEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (email.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, password,username)
        }
    }

    private fun registerUser(email: String, password: String,username: String) {
        val defaultImageUrl = "https://firebasestorage.googleapis.com/v0/b/quizapp-2607a.appspot.com/o/profile.png?alt=media&token=adc3ddb3-768a-413f-8bf8-75ee26623770"

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userId = it.uid
                        val userMap = mapOf(
                            "email" to email,
                            "password" to password,
                            "username" to username ,
                            "imageUrl" to defaultImageUrl
                        )
                        database.child("users").child(userId).setValue(userMap)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this, SiginActivity::class.java))
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
