package np.com.bimalkafle.quizonline.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import np.com.bimalkafle.quizonline.R

class SiginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sigin)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)
        val signinButton: Button = findViewById(R.id.signin_btn)
        val signupLinkTextView: TextView = findViewById(R.id.linksignnup)

        signupLinkTextView.setOnClickListener {
            startActivity(Intent(this, SigupActivity::class.java))
        }

        signinButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity_menu::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
