package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.adapters.ChatAdapter
import com.example.beintouch.adapters.OnItemClickListener
import com.example.beintouch.databinding.FragmentChatsBinding
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.User


class Chats : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachAdapterToRV()
//        fragmentManager?.beginTransaction()
//            ?.replace(R.id.container, Chat.newInstance())
//            ?.addToBackStack(null)
//            ?.commit()



    }

    private fun attachAdapterToRV() {
        chatAdapter = ChatAdapter(object : OnItemClickListener {
            override fun onItemClick(item: User) {
                mainViewModel._companionID.value = item.id
                mainViewModel.companionName.value = item.name
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.container, Chat.newInstance())
                    ?.addToBackStack(null)
                    ?.commit()


            }

        })
        binding.recyclerViewChats.adapter = chatAdapter
        mainViewModel.userList.observe(viewLifecycleOwner) {
            chatAdapter.submitList(it)
        }


    }


    companion object {
        @JvmStatic
        fun newInstance() = Chats()
    }
}