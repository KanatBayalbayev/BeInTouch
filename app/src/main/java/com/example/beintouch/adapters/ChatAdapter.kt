package com.example.beintouch.adapters

import android.content.Context
import android.text.BoringLayout
import android.util.Log
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
import com.squareup.picasso.Picasso

class ChatAdapter(
    private val listener: OnItemClickListener,
) : ListAdapter<User, ChatAdapter.ViewHolder>(Comparator()) {
    val selectedItems = mutableSetOf<String>()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        //        private val binding = ChatItemBinding.bind(view)
//        fun bind(user: User) = with(binding) {
//            userNameChatItem.text = user.name
//            Picasso.get().load(user.userProfileImage).into(userIcon)
//            if (user.online) {
//                statusChatItem.setText(R.string.statusOnline)
//            } else {
//                statusChatItem.setText(R.string.statusOffline)
//            }
//            if (user.selected) {
//                sign.visibility= View.VISIBLE
//            } else {
//                sign.visibility= View.GONE
//            }
//        }
        val name = view.findViewById<TextView>(R.id.userNameChatItem)
        val lastMessage = view.findViewById<TextView>(R.id.lastMessage)
        val lastTimeMessageSent = view.findViewById<TextView>(R.id.statusChatItem)
        val backroundForMark = view.findViewById<View>(R.id.backroundForMark)
        val buttonToDeleteChatItem = view.findViewById<ImageView>(R.id.buttonToDeleteChatItem)
        val sign = view.findViewById<ImageView>(R.id.sign)
        val userIcon = view.findViewById<ImageView>(R.id.userIcon)
        fun bind(user: User) {


        }


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
        var isEnabled = false
//        holder.bind(chatItem)
        holder.bind(chatItem)
        holder.name.text = chatItem.name
        holder.lastMessage.text = chatItem.lastMessage
        holder.lastTimeMessageSent.text = chatItem.lastTimeMessageSent
        Picasso.get().load(chatItem.userProfileImage).into(holder.userIcon)
        holder.buttonToDeleteChatItem.visibility = View.GONE

        holder.itemView.setOnLongClickListener {
            isEnabled = true
            selectedItems.add(chatItem.id)
            holder.lastTimeMessageSent.visibility = View.GONE
            holder.buttonToDeleteChatItem.visibility = View.VISIBLE
            true
        }
        holder.buttonToDeleteChatItem.setOnClickListener {
            Log.d("Tester", "Deleted: ${chatItem.name}")
            listener.onUserFromChatsDelete(chatItem)
        }

        holder.itemView.setOnClickListener {
            if (selectedItems.contains(chatItem.id)){

            }

            if (isEnabled){
                holder.lastTimeMessageSent.visibility = View.VISIBLE
                holder.buttonToDeleteChatItem.visibility = View.GONE
                isEnabled = false
            } else {
                Log.d("Tester", "Перешли в чат: ${isEnabled}")
                listener.onItemClick(chatItem)
            }
            Log.d("Tester", "Deleted: ${isEnabled}")

        }
    }





    override fun onCurrentListChanged(
        previousList: MutableList<User>,
        currentList: MutableList<User>
    ) {
        super.onCurrentListChanged(previousList, currentList)


    }
}