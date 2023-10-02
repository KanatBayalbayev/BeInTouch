package com.example.beintouch.adapters

import android.content.Context
import android.text.BoringLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beintouch.R
import com.example.beintouch.databinding.ChatItemBinding
import com.example.beintouch.presentation.User

class ChatAdapter(
    private val listener: OnItemClickListener,
) : ListAdapter<User, ChatAdapter.ViewHolder>(Comparator()) {
    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val backgroundForMark = view.findViewById<View>(R.id.backroundForMark)
        val sign = view.findViewById<ImageView>(R.id.sign)
        val userNameChatItem = view.findViewById<TextView>(R.id.userNameChatItem)
        val statusChatItem = view.findViewById<TextView>(R.id.statusChatItem)
//        private val binding = ChatItemBinding.bind(view)

//        fun bind(user: User) {
//            binding.userNameChatItem.text = user.name
//            if (user.online){
//                binding.statusChatItem.setText(R.string.statusOnline)
//            } else {
//                binding.statusChatItem.setText(R.string.statusOffline)
//            }
//            if (user.isClicked){
//                binding.backroundForMark.visibility = View.VISIBLE
//                binding.sign.visibility = View.VISIBLE
//            } else {
//                binding.backroundForMark.visibility = View.GONE
//                binding.sign.visibility = View.GONE
//            }
//        }
    }

    class Comparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.chat_item,
            parent,
            false
        )
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatItem = getItem(position)
        holder.userNameChatItem.text = chatItem.name
        if (chatItem.online) {
            holder.statusChatItem.setText(R.string.statusOnline)
        } else {
            holder.statusChatItem.setText(R.string.statusOffline)
        }


        holder.itemView.setOnClickListener {

            listener.onItemClick(chatItem)
        }

    }


    override fun onCurrentListChanged(
        previousList: MutableList<User>,
        currentList: MutableList<User>
    ) {
        super.onCurrentListChanged(previousList, currentList)

    }
}