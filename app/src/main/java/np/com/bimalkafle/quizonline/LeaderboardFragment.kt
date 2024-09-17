package np.com.bimalkafle.quizonline

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import np.com.bimalkafle.quizonline.Adapter.LeaderboardAdapter
import np.com.bimalkafle.quizonline.Class.LeaderboardEntry
import np.com.bimalkafle.quizonline.Class.User
import np.com.bimalkafle.quizonline.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {

    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var leaderboardAdapter: LeaderboardAdapter
    private val userList = mutableListOf<User>()
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference.child("leaderboard")
        binding.leaderBoardRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        leaderboardAdapter = LeaderboardAdapter(userList)
        binding.leaderBoardRecyclerView.adapter = leaderboardAdapter

        loadLeaderboardData()
    }



    private fun loadLeaderboardData() {
        val query = database.orderByChild("score")

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val leaderboardEntries = mutableListOf<LeaderboardEntry>()
                for (data in snapshot.children) {
                    val userId = data.child("userId").getValue(String::class.java)
                    val score = data.child("score").getValue(Int::class.java) ?: 0
                    if (userId != null) {
                        leaderboardEntries.add(LeaderboardEntry(userId, score))
                    }
                }

                leaderboardEntries.sortByDescending { it.score }
                displayTopUsers(leaderboardEntries.take(3))
                displayTopUsers2(leaderboardEntries.take(20))
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Leaderboard", "Error fetching leaderboard data", error.toException())
            }
        })
    }

    private fun displayTopUsers(topUsers: List<LeaderboardEntry>) {
        val rootRef = FirebaseDatabase.getInstance().reference
        topUsers.forEachIndexed { index, entry ->
            rootRef.child("users").child(entry.userId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child("username").getValue(String::class.java) ?: "N/A"
                        val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
                        Log.d("Leaderboard", "User: $username, Image: $imageUrl")
                        updateUI(index, username, entry.score, imageUrl)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("Leaderboard", "Error fetching user data", databaseError.toException())
                    }
                })
        }
    }

    private fun displayTopUsers2(topUsers: List<LeaderboardEntry>) {
        val rootRef = FirebaseDatabase.getInstance().reference
        userList.clear() // Xóa dữ liệu cũ

        Log.d("Leaderboard", "Top users size: ${topUsers.size}")
        topUsers.forEachIndexed { index, entry ->
            if (index >= 3) { // Kiểm tra vị trí từ 3 trở lên
                Log.d("Leaderboard", "Fetching user data for index: $index, userId: ${entry.userId}")

                rootRef.child("users").child(entry.userId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val username = snapshot.child("username").getValue(String::class.java) ?: "N/A"
                            val imageUrl = snapshot.child("imageUrl").getValue(String::class.java) ?: ""
                            Log.d("Leaderboard", "User: $username, Image: $imageUrl")

                            // Thêm người dùng mới vào danh sách userList
                            userList.add(User(index.toString(), username, entry.score, imageUrl))
                            Log.d("Leaderboard", "User added to list: ${userList.size}")

                            // Thông báo cho adapter biết rằng dữ liệu đã thay đổi
                            leaderboardAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            Log.e("Leaderboard", "Error fetching user data", databaseError.toException())
                        }
                    })
            }
        }

        Log.d("Leaderboard", "Finished processing users.")
    }





    private fun updateUI(index: Int, username: String, score: Int, imageUrl: String) {
        when (index) {
            0 -> {
                binding.tvRank1Name.text = username
                binding.tvRank1Points.text = score.toString()
                if (imageUrl.isNotEmpty()) {
                    Glide.with(requireContext()).load(imageUrl).into(binding.ivRank1)
                }
            }
            1 -> {
                binding.tvRank2Name.text = username
                binding.tvRank2Points.text = score.toString()
                if (imageUrl.isNotEmpty()) {
                    Glide.with(requireContext()).load(imageUrl).into(binding.ivRank2)
                }
            }
            2 -> {
                binding.tvRank3Name.text = username
                binding.tvRank3Points.text = score.toString()
                if (imageUrl.isNotEmpty()) {
                    Glide.with(requireContext()).load(imageUrl).into(binding.ivRank3)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
