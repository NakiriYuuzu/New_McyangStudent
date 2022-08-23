package tw.edu.studentmcyang.activity.main.sign.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import tw.edu.studentmcyang.activity.main.sign.model.Sign
import tw.edu.studentmcyang.databinding.RvlayoutType01Binding

class SignAdapter(
    private val itemClickListener: OnItemClickListener
): ListAdapter<Sign, SignAdapter.SignViewHolder>(ItemCallback()) {

    class ItemCallback: DiffUtil.ItemCallback<Sign>() {
        override fun areItemsTheSame(oldItem: Sign, newItem: Sign): Boolean {
            return oldItem.Sign_id == newItem.Sign_id
        }

        override fun areContentsTheSame(oldItem: Sign, newItem: Sign): Boolean {
            return oldItem == newItem
        }

    }

    class SignViewHolder(val binding: RvlayoutType01Binding, private val itemClickListener: OnItemClickListener): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.rvlayoutBtnSubmit.setOnClickListener {
                itemClickListener.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SignViewHolder {
        return SignViewHolder(
            RvlayoutType01Binding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        , itemClickListener
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SignViewHolder, position: Int) {
        val sign = currentList[position]

        if (sign.C_Name.length > 12) {
            holder.binding.rvlayoutTextMain.text = sign.C_Name.substring(0, 12) + "..."
        } else {
            holder.binding.rvlayoutTextMain.text = sign.C_Name
        }

        holder.binding.rvlayoutTextSub.text = sign.T_Name
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}