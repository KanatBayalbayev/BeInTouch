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
    private val showTrash: (Boolean) -> Unit
) : ListAdapter<User, ChatAdapter.ViewHolder>(Comparator()) {
    private var isEnabled = false
    private val itemSelectedList = mutableListOf<Int>()
    private val chatItemsSelectedList = mutableListOf<User>()

    fun getChatItemsToRemove(): List<User>{
        return chatItemsSelectedList.toList()
    }
    fun getPositions(): List<Int>{
        return itemSelectedList.toList()
    }


    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val backroundForMark = view.findViewById<View>(R.id.backroundForMark)
        val sign = view.findViewById<ImageView>(R.id.sign)
        val userNameChatItem = view.findViewById<TextView>(R.id.userNameChatItem)
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
        holder.itemView.setOnClickListener {

            if (itemSelectedList.contains(position)) {
                itemSelectedList.removeAt(position)
                chatItemsSelectedList.remove(chatItem)
                holder.backroundForMark.visibility = View.GONE
                holder.sign.visibility = View.GONE
                chatItem.isClicked = false
                if (itemSelectedList.isEmpty()) {
                    showTrash(false)
                    isEnabled = false
                }
            } else if (isEnabled) {
                selectItem(holder, chatItem, position)

            }
            listener.onItemClick(getChatItemsToRemove(), chatItem)
        }
        holder.itemView.setOnLongClickListener {

//            listener.onItemLongClick(chatItem)
            selectItem(holder, chatItem, position)

            true
        }
    }

    private fun selectItem(holder: ChatAdapter.ViewHolder, chatItem: User?, position: Int) {
        isEnabled = true
        itemSelectedList.add(position)
        if (chatItem != null) {
            chatItemsSelectedList.add(chatItem)
        }
        chatItem?.isClicked = true
        holder.backroundForMark.visibility = View.VISIBLE
        holder.sign.visibility = View.VISIBLE
        showTrash(true)

    }


    override fun onCurrentListChanged(
        previousList: MutableList<User>,
        currentList: MutableList<User>
    ) {
        super.onCurrentListChanged(previousList, currentList)

    }
}