package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.beintouch.adapters.ChatAdapter
import com.example.beintouch.adapters.OnItemClickListener
import com.example.beintouch.databinding.FragmentChatsBinding
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.User


class Chats : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private val loginViewModel: MainViewModel by activityViewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachAdapterToRV()




    }

    private fun attachAdapterToRV() {
        chatAdapter = ChatAdapter(object : OnItemClickListener {
            override fun onItemClick(item: User) {

            }

        })
        binding.recyclerViewChats.adapter = chatAdapter

    }


    companion object {
        @JvmStatic
        fun newInstance() = Chats()
    }
}