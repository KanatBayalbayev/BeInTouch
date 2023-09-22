package com.example.beintouch.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.beintouch.R
import com.example.beintouch.databinding.ChatItemBinding
import com.example.beintouch.presentation.Message
import com.example.beintouch.presentation.User

class MessagesAdapter(
    private val userID: String
) : ListAdapter<Message, MessagesAdapter.ViewHolder>(Comparator()) {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var textMessage: TextView = view.findViewById(R.id.textMessage)

        fun bind(message: Message) {
            textMessage.text = message.textMessage


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
        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderID == userID){
            USER_MESSAGE
        } else {
            COMPANION_MESSAGE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
        holder.bind(message)

    }

    companion object {
        private const val USER_MESSAGE = 1
        private const val COMPANION_MESSAGE = 2

    }
}