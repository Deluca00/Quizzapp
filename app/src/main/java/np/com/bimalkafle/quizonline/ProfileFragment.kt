package np.com.bimalkafle.quizonline

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import np.com.bimalkafle.quizonline.Activity.QuestionActivity
import np.com.bimalkafle.quizonline.Activity.SiginActivity
import np.com.bimalkafle.quizonline.Activity.editprofileActivity
import np.com.bimalkafle.quizonline.Adapter.QuizListuserAdapter
import np.com.bimalkafle.quizonline.Class.QuizModel
import np.com.bimalkafle.quizonline.Class.User
import np.com.bimalkafle.quizonline.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: StorageReference
    private lateinit var quizModelList: MutableList<QuizModel>
    private lateinit var adapter: QuizListuserAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance().reference

        quizModelList = mutableListOf()


        loadUserProfile()
        loadLeaderboardData()

        binding.QuestionBtn.setOnClickListener {
            startActivity(Intent(requireContext(), QuestionActivity::class.java))
        }

        binding.edtProfile.setOnClickListener {
            startActivity(Intent(requireContext(), editprofileActivity::class.java))
        }
        binding.cvSignOut.setOnClickListener{
            startActivity(Intent(requireContext(), SiginActivity::class.java))
        }


        getDataFromuserFirebase()

    }

    private fun setupRecyclerView() {
        adapter = QuizListuserAdapter(quizModelList)
        binding.recyclerView2.layoutManager = LinearLayoutManager(context)
        binding.recyclerView2.adapter = adapter
    }

    private fun loadUserProfile() {
        val userId = auth.currentUser?.uid
        userId?.let { uid ->
            database.child("users").child(uid).get().addOnSuccessListener { dataSnapshot ->
                val userName = dataSnapshot.child("username").value.toString()
                val email = dataSnapshot.child("email").value.toString()
                val profilePicUrl = dataSnapshot.child("imageUrl").value.toString()
                binding.tvUserName.text = userName
                binding.tvEmailId.text = email
                Picasso.get().load(profilePicUrl).into(binding.userProfilePic)
            }.addOnFailureListener {
                // Handle error
            }
        }
    }

    private fun loadLeaderboardData() {
        val leaderboardRef = FirebaseDatabase.getInstance().getReference("leaderboard")

        leaderboardRef.orderByChild("score").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leaderboardList = mutableListOf<User>()
                snapshot.children.forEach { data ->
                    val user = data.getValue(User::class.java)
                    user?.let { leaderboardList.add(it) }
                }

                // Sắp xếp danh sách xếp hạng theo điểm số (từ cao đến thấp)
                leaderboardList.sortByDescending { it.score }

                val currentUser = leaderboardList.find { it.userId == auth.currentUser?.uid }
                currentUser?.let { user ->
                    // Hiển thị xếp hạng và điểm số của người dùng hiện tại trong TextView
                    binding.tvUserRanking.text = "${leaderboardList.indexOf(user) + 1}"
                    binding.tvUserPoints.text = "${user.score}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Xử lý khi có lỗi xảy ra
            }
        })
    }


    private fun getDataFromuserFirebase() {
        val currentUserId = getCurrentUserId()

        FirebaseDatabase.getInstance().reference.child("question")
            .get()
            .addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    for (snapshot in dataSnapshot.children) {
                        val quizModel = snapshot.getValue(QuizModel::class.java)
                        if (quizModel != null && quizModel.id == currentUserId) { // Kiểm tra ID
                            quizModelList.add(quizModel)
                        }
                    }
                }
                setupRecyclerView()
            }
    }

    private fun getCurrentUserId(): String? {
        val user = FirebaseAuth.getInstance().currentUser
        return user?.uid
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()  // Reload user profile data when fragment is resumed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}