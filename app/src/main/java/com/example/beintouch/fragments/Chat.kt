package com.example.beintouch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.beintouch.R
import com.example.beintouch.adapters.MessagesAdapter
import com.example.beintouch.databinding.ChatBinding
import com.example.beintouch.presentation.ChatViewModel
import com.example.beintouch.presentation.ChatViewModelFactory
import com.example.beintouch.presentation.Message
import com.example.beintouch.presentation.User


class Chat : Fragment() {
    private lateinit var binding: ChatBinding
    private lateinit var messagesAdapter: MessagesAdapter


    private lateinit var currentUserID: String
    private lateinit var companionUserID: String
    private lateinit var currentUser: User
    private lateinit var chatViewModel: ChatViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserID = arguments?.getString(KEY_CURRENT_USER_ID).toString()
        companionUserID = arguments?.getString(KEY_COMP_USER_ID).toString()
        val viewModelFactory = ChatViewModelFactory(currentUserID, companionUserID)
        chatViewModel = ViewModelProvider(this, viewModelFactory)[ChatViewModel::class.java]
        binding = ChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToChatsFromChat()
        messagesAdapter = MessagesAdapter(currentUserID)
        binding.testRv.layoutManager = LinearLayoutManager(requireContext())
        binding.testRv.adapter = messagesAdapter

        observeViewModel()
        binding.buttonToSendMessage.setOnClickListener {
            val textMessage = binding.inputMessageFromUser.text.toString().trim()
            val message = Message(textMessage, currentUserID, companionUserID)
            chatViewModel.sendMessage(message, currentUser)
        }

    }

    override fun onResume() {
        super.onResume()
        chatViewModel.setUserOnline(true)

    }

    override fun onPause() {
        super.onPause()
        chatViewModel.setUserOnline(false)
    }

    private fun backToChatsFromChat() {
        binding.buttonToBackToChatsFromChat.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Chats.newInstance(currentUserID))
                ?.commit()
        }
    }

    private fun observeViewModel() {
        chatViewModel.messagesList.observe(viewLifecycleOwner) {
            messagesAdapter.submitList(it)
        }
        chatViewModel.companionUser.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.companionUserName.text = it.name
                if (it.online) {
                    binding.statusCompUserChat.setText(R.string.statusOnline)
                    binding.statusCompUserChat.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.greenOnline
                        )
                    )
                } else {
                    binding.statusCompUserChat.setText(R.string.statusOffline)
                    binding.statusCompUserChat.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.redOffline
                        )
                    )
                }

            }
        }
        chatViewModel.currUser.observe(viewLifecycleOwner){
            if (it != null) {
                currentUser = it
            }
        }


    }


    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_COMP_USER_ID = "comp_user_id"

        @JvmStatic
        fun newInstance(parameterUserID: String, parameterCompUserID: String): Chat {
            val fragment = Chat()
            val args = Bundle()
            args.putString(KEY_CURRENT_USER_ID, parameterUserID)
            args.putString(KEY_COMP_USER_ID, parameterCompUserID)
            fragment.arguments = args
            return fragment
        }
    }
}