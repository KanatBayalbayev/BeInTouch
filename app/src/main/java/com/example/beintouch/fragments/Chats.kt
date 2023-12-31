package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
import com.squareup.picasso.Picasso


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
        Log.d("ChatNoConnection", currentUserID)

        activity?.let {
            (it as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        setHasOptionsMenu(true)
        attachAdapterToRV()
        observeViewModel()
        closeAlertDialog()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                openProfileFragment(currentUserID)
                true
            }

            R.id.add_user -> {
                dialogSearchUser()
                true
            }

            R.id.logout -> {
                mainViewModel.logout()
                openLoginFragment()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }

    private fun overLayAndDialogRemoveFriendOff() {
        binding.dialogRemoveFriend.visibility = View.GONE
        binding.overlayView.visibility = View.GONE
        binding.overlayView.isClickable = true
    }

    private fun overLayAndDialogAddFriendOff() {
        binding.dialogSearchUser.visibility = View.GONE
        binding.overlayView.visibility = View.GONE
        binding.overlayView.isClickable = true
    }

    private fun overLayAndDialogRemoveFriendOn() {
        binding.dialogRemoveFriend.visibility = View.VISIBLE
        binding.overlayView.visibility = View.VISIBLE
        binding.overlayView.isClickable = false
    }

    private fun attachAdapterToRV() {
        chatAdapter = ChatAdapter(object : OnItemClickListener {
            override fun onItemClick(user: User) {
                openChatFragment(currentUserID, user.id)
            }

            override fun onUserFromChatsDelete(user: User, isDialogShown: Boolean) {
                if (isDialogShown) {
                    overLayAndDialogRemoveFriendOn()
                }

                binding.buttonToDeleteFriend.setOnClickListener {
                    mainViewModel.deleteChat(currentUserID, user.id)
                    overLayAndDialogRemoveFriendOff()
                }

                binding.buttonToCancelDeletingFriend.setOnClickListener {
                    overLayAndDialogRemoveFriendOff()
                }
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
                openLoginFragment()
            }
        }, currentUserID)
        mainViewModel.userName.observe(viewLifecycleOwner) {
            binding.toolbar.title = it

        }
    }


    private fun openChatFragment(currentUserId: String, compUserId: String) {
        val fragment = Chat.newInstance(currentUserId, compUserId, true)
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.commit()
    }

    private fun openLoginFragment() {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.container, Login.newInstance())
            ?.commit()
    }

    private fun openProfileFragment(currentUserId: String) {
        val fragment = Profile.newInstance(currentUserId)
        val transaction = fragmentManager?.beginTransaction()
        transaction?.replace(R.id.container, fragment)
        transaction?.commit()
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
            }
        }
        mainViewModel.foundUser.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                binding.notFound.visibility = View.GONE
                binding.buttonToAddUserToChats.visibility = View.VISIBLE
                binding.foundUserIconCIV.visibility = View.VISIBLE
                binding.foundUser.visibility = View.VISIBLE

                binding.foundUserName.text = user.name
                binding.buttonToAddUserToChats.setOnClickListener {
                    mainViewModel.addFoundUserToChats(user)
                    overLayAndDialogAddFriendOff()
                }

                val hasProfileImage = user.userProfileImage != ""
                binding.foundUserIconCIV.visibility =
                    if (hasProfileImage) View.VISIBLE else View.GONE
                binding.foundUserIcon.visibility = if (hasProfileImage) View.GONE else View.VISIBLE

                if (hasProfileImage) {
                    Picasso.get().load(user.userProfileImage).into(binding.foundUserIconCIV)
                }
            }
        }
    }

    private fun closeAlertDialog() {
        binding.buttonToCloseDialog.setOnClickListener {
            overLayAndDialogAddFriendOff()
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