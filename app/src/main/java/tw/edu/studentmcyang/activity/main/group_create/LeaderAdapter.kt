package tw.edu.studentmcyang.activity.main.group_create

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tw.edu.studentmcyang.databinding.RylayoutListStatusBinding

class LeaderAdapter(val onItemClickListener: OnItemClickListener): RecyclerView.Adapter<LeaderAdapter.LeaderViewHolder>() {

    class LeaderViewHolder(val binding: RylayoutListStatusBinding, val onItemClickListener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rootButton.setOnClickListener {
                onItemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<TeamDto>() {
        override fun areItemsTheSame(oldItem: TeamDto, newItem: TeamDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TeamDto, newItem: TeamDto): Boolean {
            if (oldItem.id != newItem.id) return false
            if (oldItem.teamName != newItem.teamName) return false
            if (oldItem.isLeader != newItem.isLeader) return false
            return true
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderViewHolder {
        return LeaderViewHolder(
            RylayoutListStatusBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClickListener = onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: LeaderViewHolder, position: Int) {
        val teamList = differ.currentList[position]

        holder.binding.apply {
            if (teamList.teamName.length < 2)iconText.text = teamList.teamName
            else iconText.text = teamList.teamName.substring(teamList.teamName.length - 2)

            if (teamList.isLeader)eventText.text = "選擇"
            // else eventText.text = "隊長"

            eventText.setTextColor(Color.parseColor("#46a5ff"))
            eventButton.strokeColor = Color.parseColor("#46a5ff")

            textName.text = teamList.teamName
            textId.text = ""
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}