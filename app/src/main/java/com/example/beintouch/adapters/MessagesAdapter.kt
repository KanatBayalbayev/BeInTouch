package com.example.beintouch.adapters

import android.content.Context
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessagesAdapter(
    private val userID: String,
    private val compID: String,
    context: Context
) : ListAdapter<Message, MessagesAdapter.ViewHolder>(Comparator()) {
    var list = 0
    private val mContext: Context = context


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
         val textViewMessage: TextView = view.findViewById(R.id.textMessage)
         val unreadMessage: TextView = view.findViewById(R.id.messageunRead)
         val readMessage: TextView = view.findViewById(R.id.messageRead)
         val sentMessageTime: TextView = view.findViewById(R.id.sentMessageTime)
         val messageSeen: TextView = view.findViewById(R.id.messageSeen)

//        fun bind(message: Message, userid: String, isMessageRead: Boolean, compid: String) {
//            textViewMessage.text = message.textMessage
//            sentMessageTime.text = message.timestamp
//        }

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

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        Log.d("MessagesAdapter", "UserID: $userID")
        return if (message.senderID == userID) {
            USER_MESSAGE
        } else {
            COMPANION_MESSAGE
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
//        holder.bind(message, userID, isReadMessage, compID)
        holder.textViewMessage.text = message.textMessage
        holder.sentMessageTime.text = message.timestamp
        if (position == currentList.size - 1){
            if (message.senderID == userID){
                if (message.isseen){
                    holder.messageSeen.text = mContext.resources.getString(R.string.seen)
                } else {
                    holder.messageSeen.text = mContext.resources.getString(R.string.delivered)
                }
            }
        } else {
            holder.messageSeen.visibility = View.GONE
        }

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