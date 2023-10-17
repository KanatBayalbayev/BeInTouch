package com.example.beintouch.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beintouch.R
import com.example.beintouch.presentation.Message

class MessAdapter(
    private val data: List<Message>,
    private val userID: String,
    private val compID: String,
) :
    RecyclerView.Adapter<MessAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var layout = 0
        layout = if (viewType == USER_MESSAGE) {
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.textViewMessage.text = item.textMessage

        if (position == data.size - 1){
            if (item.senderID == userID){
                if (item.isseen){
                    holder.sentMessageTime.text = "Seen"
                } else {
                    holder.sentMessageTime.text = "Delivered"
                }
            }
        } else {
            holder.sentMessageTime.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val textViewMessage: TextView = itemView.findViewById(R.id.textMessage)
         val sentMessageTime: TextView = itemView.findViewById(R.id.sentMessageTime)

    }

    override fun getItemViewType(position: Int): Int {
        val message = data[position]
        Log.d("MessagesAdapter", "UserID: $userID")
        return if (message.senderID == userID) {
           USER_MESSAGE
        } else {
            COMPANION_MESSAGE
        }
    }

    companion object {
        private const val USER_MESSAGE = 1
        private const val COMPANION_MESSAGE = 2

    }
}