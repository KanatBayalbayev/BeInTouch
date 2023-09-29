package com.example.beintouch.adapters

import com.example.beintouch.fragments.Chat
import com.example.beintouch.presentation.User

interface OnItemClickListener {
    fun onItemClick(item: List<User>, user: User)

//    fun onItemLongClick(element: User)
}