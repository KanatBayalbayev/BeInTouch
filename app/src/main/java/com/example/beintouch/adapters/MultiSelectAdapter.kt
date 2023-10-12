package com.example.beintouch.adapters

import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.beintouch.R
import com.example.beintouch.presentation.User

class MultiSelectAdapter(
    private var users: MutableList<User>,
    private val showTrash: (Boolean) -> Unit
) :
    RecyclerView.Adapter<MultiSelectAdapter.ViewHolder>() {
    private var isEnabled = false
    private val itemSelectedList = mutableListOf<Int>()


    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.userNameChatItem)
        val backroundForMark = view.findViewById<View>(R.id.backroundForMark)
        val sign = view.findViewById<ImageView>(R.id.sign)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = users[position]
        holder.name.text = item.name
        holder.backroundForMark.visibility = View.GONE
        holder.sign.visibility = View.GONE


        holder.itemView.setOnLongClickListener {
            selectItem(holder, item, position)
            true
        }

        holder.itemView.setOnClickListener {
            if (itemSelectedList.contains(position)) {
                itemSelectedList.removeAt(position)
                holder.backroundForMark.visibility = View.GONE
                holder.sign.visibility = View.GONE
                item.selected = false
                if (itemSelectedList.isEmpty()){
                    showTrash(false)
                    isEnabled = false
                }


            } else if (isEnabled){
                selectItem(holder, item, position)

            }
        }
    }

    private fun selectItem(holder: MultiSelectAdapter.ViewHolder, item: User, position: Int) {
        isEnabled = true
        itemSelectedList.add(position)
        item.selected = true
        holder.backroundForMark.visibility = View.VISIBLE
        holder.sign.visibility = View.VISIBLE
        showTrash(true)
    }

    override fun getItemCount(): Int {
        return users.size
    }

   fun deleteSelectedItems() {
       if (itemSelectedList.isNotEmpty()){
           users.removeAll{item -> item.selected}
           isEnabled = false
           itemSelectedList.clear()
       }
       notifyDataSetChanged()
   }

}