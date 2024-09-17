package np.com.bimalkafle.quizonline.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import np.com.bimalkafle.quizonline.R
import np.com.bimalkafle.quizonline.Class.User

class LeaderboardAdapter(private val userList: List<User>) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRank: TextView = itemView.findViewById(R.id.tvLeaderboardRank)
        val tvName: TextView = itemView.findViewById(R.id.tvUserRanking)
        val tvScore: TextView = itemView.findViewById(R.id.tvUserPoints)
        val ivProfilePic: ImageView = itemView.findViewById(R.id.userProfilePic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.leader_board_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.tvRank.text = (position + 4).toString()
        holder.tvName.text = user.username
        holder.tvScore.text = user.score.toString()
        Glide.with(holder.itemView.context)
            .load(user.imageUrl)
            .into(holder.ivProfilePic)
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
