package com.example.beintouch.adapters

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
import com.example.beintouch.presentation.Message

class MessagesAdapter(
    private val userID: String,
    private val compID: String,
): ListAdapter<Message, MessagesAdapter.ViewHolder>(Comparator()) {



    class ViewHolder(view: View, private val viewType: Int) : RecyclerView.ViewHolder(view) {
        private val textViewMessage: TextView = view.findViewById(R.id.textMessage)
        private val unreadMessage: ImageView = view.findViewById(R.id.unreadMessage)
        private val readMessage: ImageView = view.findViewById(R.id.readMessage)

        fun bind(message: Message, compID: String) {
            if (message.companionID == compID){
                unreadMessage.visibility = View.GONE
                readMessage.visibility = View.VISIBLE
            } else {
                unreadMessage.visibility = View.VISIBLE
                readMessage.visibility = View.GONE
            }

            textViewMessage.text = message.textMessage
        }

    }

    class Comparator : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layout = 0
        layout = if (viewType == USER_MESSAGE){
            R.layout.user_message
        } else {
            R.layout.comp_message
        }
        val view = LayoutInflater.from(parent.context).inflate(
            layout,
            parent,
            false
        )
        return ViewHolder(view, viewType)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        Log.d("MessagesAdapter", "UserID: $userID")
        return if (message.senderID == userID){
            USER_MESSAGE
        } else {
            COMPANION_MESSAGE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message, compID)

    }

    override fun onCurrentListChanged(
        previousList: MutableList<Message>,
        currentList: MutableList<Message>
    ) {
        super.onCurrentListChanged(previousList, currentList)

    }

    companion object {
        private const val USER_MESSAGE = 1
        private const val COMPANION_MESSAGE = 2

    }
}