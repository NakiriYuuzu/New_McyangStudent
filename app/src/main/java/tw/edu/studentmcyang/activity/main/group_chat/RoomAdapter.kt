package tw.edu.studentmcyang.activity.main.group_chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import tw.edu.studentmcyang.databinding.RvlayoutType01Binding

class RoomAdapter(val onItemClickListener: OnItemClickListener) : RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    class RoomViewHolder(val binding: RvlayoutType01Binding, onItemClickListener: OnItemClickListener) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rvlayoutBtnSubmit.setOnClickListener {
                onItemClickListener.onClick(adapterPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    private val differCallback = object : DiffUtil.ItemCallback<RoomDto>() {
        override fun areItemsTheSame(oldItem: RoomDto, newItem: RoomDto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: RoomDto, newItem: RoomDto): Boolean {
            if (oldItem.groupChat_id != newItem.groupChat_id) return false
            if (oldItem.chatTitle != newItem.chatTitle) return false
            if (oldItem.leaderName != newItem.leaderName) return false
            return true
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        return RoomViewHolder(
            RvlayoutType01Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onItemClickListener
        )
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.binding.apply {
            val room = differ.currentList[position]

            val subText = "[隊長] " + room.leaderName

            rvlayoutTextMain.text = room.chatTitle
            rvlayoutTextSub.text = subText
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}