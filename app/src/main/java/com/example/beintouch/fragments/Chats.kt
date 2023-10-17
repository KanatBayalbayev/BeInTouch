package com.example.beintouch.fragments

import android.app.AlertDialog
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
import com.example.beintouch.adapters.MultiSelectAdapter
import com.example.beintouch.adapters.OnItemClickListener
import com.example.beintouch.databinding.FragmentChatsBinding
import com.example.beintouch.presentation.AuthStateListener
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.User
import com.google.firebase.auth.FirebaseUser


class Chats : Fragment() {
    private var mainMenu: Menu? = null

    private lateinit var binding: FragmentChatsBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var currentUserID: String
    private  var userName: String = ""
    private var isOptionsMenuOpened = false


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

        activity?.let {
            (it as AppCompatActivity).setSupportActionBar(binding.toolbar)
        }
        setHasOptionsMenu(true)
        attachAdapterToRV()
        observeViewModel()
        logOut()

        showDialogSearchUser()
        closeAlertDialog()
//        showOptionsMenu()
        binding.trash.setOnClickListener {
//            val alertDialog = AlertDialog.Builder(requireContext())
//            alertDialog.setTitle("Delete")
//            alertDialog.setMessage("Do you want to delete the items")
//            alertDialog.setPositiveButton("Delete") { _, _ ->
//                usersAdapter.deleteSelectedItems()
//                showTrash(false)
//
//            }
//            alertDialog.setNegativeButton("Cancel") { _, _ ->
//            }
//            alertDialog.show()
        }
//        showTrash()


    }

//    private fun showTrash(show: Boolean = false) {
//        if (show) {
//            binding.trash.visibility = View.VISIBLE
//        } else {
//            binding.trash.visibility = View.GONE
//        }
//    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item -> {
                mainViewModel.logout()
                openLoginFragment()
                true
            }
            R.id.add_user -> {
                dialogSearchUser()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }


    }

    private fun attachAdapterToRV() {
        chatAdapter = ChatAdapter(object : OnItemClickListener {
            override fun onItemClick(user: User) {
                openChatFragment(currentUserID, user.id)


            }

            override fun onUserFromChatsDelete(user: User) {
                mainViewModel.deleteChat(currentUserID, user.id)

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

    private fun logOut() {
        binding.buttonToLogOut.setOnClickListener {

            mainViewModel.logout()
            openLoginFragment()
        }

    }

    private fun showDialogSearchUser() {
        binding.findUser.setOnClickListener {
            dialogSearchUser()
        }
    }

    private fun showOptionsMenu() {
        binding.optionsMenu.setOnClickListener {
            isOptionsMenuOpened = !isOptionsMenuOpened
            if (isOptionsMenuOpened) {
                binding.options.visibility = View.VISIBLE
            } else {
                binding.options.visibility = View.GONE
            }

            Log.d("CHatsFrag", isOptionsMenuOpened.toString())
        }
    }


    private fun dialogSearchUser() {
        isOptionsMenuOpened = !isOptionsMenuOpened
        Log.d("CHatsFrag", isOptionsMenuOpened.toString())
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
        mainViewModel.foundUser.observe(viewLifecycleOwner) { user ->
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
            isOptionsMenuOpened = !isOptionsMenuOpened
            Log.d("CHatsFrag", isOptionsMenuOpened.toString())
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