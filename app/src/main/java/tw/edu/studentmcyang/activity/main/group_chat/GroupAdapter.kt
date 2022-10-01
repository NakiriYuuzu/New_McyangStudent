package tw.edu.studentmcyang.activity.main.group_chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tw.edu.studentmcyang.databinding.RvlayoutType01Binding

class GroupAdapter(val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(val binding: RvlayoutType01Binding, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rvlayoutBtnSubmit.setOnClickListener {
                onItemClickListener.onClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    private val differCallback = object : DiffUtil.ItemCallback<GroupDto>() {
        override fun areItemsTheSame(oldItem: GroupDto, newItem: GroupDto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GroupDto, newItem: GroupDto): Boolean {
            if (oldItem.teamDescId != newItem.teamDescId) return false
            if (oldItem.teamLeaderId != newItem.teamLeaderId) return false
            if (oldItem.teamDoc != newItem.teamDoc) return false
            if (oldItem.sid != newItem.sid) return false
            if (oldItem.sName != newItem.sName) return false
            if (oldItem.isLeader != newItem.isLeader) return false
            return true
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            RvlayoutType01Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.binding.apply {
            val group = differ.currentList[position]

            val subText = if (group.isLeader == "true") "[隊長] " + group.sName else "[組員] " + group.sName

            rvlayoutTextMain.text = group.teamDoc
            rvlayoutTextSub.text = subText
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}