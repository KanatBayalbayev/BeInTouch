package com.example.beintouch.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.adapters.ChatAdapter
import com.example.beintouch.adapters.OnItemClickListener
import com.example.beintouch.databinding.FragmentChatsBinding
import com.example.beintouch.presentation.AuthStateListener
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.User
import com.google.firebase.auth.FirebaseUser


class Chats : Fragment() {
    private lateinit var binding: FragmentChatsBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currentUserID: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserID = arguments?.getString(KEY_CURRENT_USER_ID).toString()
        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        attachAdapterToRV()
        observeViewModel()
        logOut()
        showDialogSearchUser()
        closeAlertDialog()



    }

    private fun attachAdapterToRV() {
        chatAdapter = ChatAdapter(object : OnItemClickListener {
            override fun onItemClick(item: User) {
                openChatFragment(currentUserID, item.id)
            }

        })
        binding.recyclerViewChats.adapter = chatAdapter

        mainViewModel.userList.observe(viewLifecycleOwner) {
            chatAdapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.setUserOnline(true)

    }

    override fun onPause() {
        super.onPause()
        mainViewModel.setUserOnline(false)
    }


    private fun observeViewModel() {
        mainViewModel.setAuthStateListener(object : AuthStateListener {
            override fun onUserAuthenticated(user: FirebaseUser?) {

            }

            override fun onUserUnauthenticated() {
                mainViewModel.setUserOnline(false)

            }
        }, currentUserID)
        mainViewModel.userName.observe(viewLifecycleOwner) {
            binding.userNameAccount.text = it
        }
    }


    private fun openChatFragment(currentUserId: String, compUserId: String) {
        val fragment = Chat.newInstance(currentUserId, compUserId)
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.commit()
    }

    private fun openLoginFragment() {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.container, Login.newInstance())
            ?.commit()
    }

    private fun logOut() {
        binding.buttonToLogOut.setOnClickListener {
            mainViewModel.logout()
            openLoginFragment()
        }

    }

    private fun showDialogSearchUser(){
        binding.findUser.setOnClickListener {
            dialogSearchUser()
        }
    }
    private fun dialogSearchUser() {
        binding.dialogSearchUser.visibility = View.VISIBLE
        binding.overlayView.visibility = View.VISIBLE
        binding.overlayView.isClickable = true
        binding.searchUser.setOnClickListener {
            val user = binding.inputSearchUser.text.toString().trim()
            if (user.isEmpty()) {
                Toast.makeText(requireContext(), "Enter user email!", Toast.LENGTH_LONG).show()
            } else {
                mainViewModel.findUser(user)
                binding.buttonToAddUserToChats.visibility = View.VISIBLE
                binding.foundUserIcon.visibility = View.VISIBLE
            }
        }
        mainViewModel.foundUser.observe(viewLifecycleOwner){user ->
            if (user != null) {
                binding.foundUserName.text = user.name
                binding.buttonToAddUserToChats.setOnClickListener {
                    mainViewModel.addFoundUserToChats(user)
                }
            }
        }

    }
    private fun closeAlertDialog() {
        binding.buttonToCloseDialog.setOnClickListener {
            binding.dialogSearchUser.visibility = View.GONE
            binding.overlayView.visibility = View.GONE
            binding.overlayView.isClickable = false
        }

    }


    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"

        @JvmStatic
        fun newInstance(parameterUserID: String = ""): Chats {
            val fragment = Chats()
            val args = Bundle()
            args.putString(KEY_CURRENT_USER_ID, parameterUserID)
            fragment.arguments = args
            return fragment
        }
    }
}