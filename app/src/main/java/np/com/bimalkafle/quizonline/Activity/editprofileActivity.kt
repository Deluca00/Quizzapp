package np.com.bimalkafle.quizonline.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import np.com.bimalkafle.quizonline.R

class editprofileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var imageUri: Uri
    private lateinit var editImage:ImageView
    private lateinit var etUserName :EditText
    private lateinit var btnSave : Button

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)

        editImage = findViewById(R.id.editImage)
        etUserName = findViewById(R.id.etUserName)
        btnSave = findViewById(R.id.btnSave)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        // Load current profile image
        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            database.child("users").child(uid).get().addOnSuccessListener { dataSnapshot ->
                val userName = dataSnapshot.child("username").value.toString()
                val profilePicUrl = dataSnapshot.child("imageUrl").value.toString()
                etUserName.setText(userName)
                Picasso.get().load(profilePicUrl).into(editImage)
            }
        }

        // Set up image picker
        editImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        // Handle save button click
        btnSave.setOnClickListener {
            val newUserName = etUserName.text.toString()
            if (newUserName.isEmpty()) {
                etUserName.error = "Name is required"
                return@setOnClickListener
            }

            userId?.let { uid ->
                if (::imageUri.isInitialized) {
                    val fileRef = storage.child("users/$uid/profile.jpg")
                    fileRef.putFile(imageUri).addOnSuccessListener {
                        fileRef.downloadUrl.addOnSuccessListener { uri ->
                            val profilePicUrl = uri.toString()
                            updateUserProfile(uid, newUserName, profilePicUrl)
                        }
                    }
                } else {
                    database.child("users").child(uid).child("username").setValue(newUserName)
                    Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserProfile(uid: String, newUserName: String, profilePicUrl: String) {
        val userUpdates = mapOf(
            "username" to newUserName,
            "imageUrl" to profilePicUrl
        )
        database.child("users").child(uid).updateChildren(userUpdates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                finish()  // Close activity
            } else {
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            editImage.setImageBitmap(bitmap)
        }
    }
}