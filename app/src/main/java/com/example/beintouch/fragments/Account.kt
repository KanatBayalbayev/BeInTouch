//package com.example.beintouch.fragments
//
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.Menu
//import android.view.MenuInflater
//import android.view.MenuItem
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.fragment.app.activityViewModels
//import com.example.beintouch.R
//import com.example.beintouch.adapters.ViewPageAdapter
//import com.example.beintouch.databinding.FragmentAccountBinding
//import com.example.beintouch.presentation.AuthStateListener
//import com.example.beintouch.presentation.MainViewModel
//import com.google.android.material.tabs.TabLayoutMediator
//import com.google.firebase.auth.FirebaseUser
//
//
//class Account : Fragment() {
//    private lateinit var binding: FragmentAccountBinding
//    private val mainViewModel: MainViewModel by activityViewModels()
//    private lateinit var currentUserID: String
//
//    private val listOfFragments = listOf(
//        Chats.newInstance(currentUserID),
//        Users.newInstance()
//    )
//    private val listOfTabs = listOf(
//        "Chats",
//        "Users"
//    )
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//        currentUserID = arguments?.getString(KEY_CURRENT_USER_ID).toString()
////        val factory = ChatViewModelFactory()
////        mainViewModel = ViewModelProvider(this, factory)[MainViewModel::class.java]
//    }
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = FragmentAccountBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        observeViewModel()
//        attachAdapterToViewPager()
//
//    }
//
//    private fun observeViewModel(){
//        mainViewModel.setAuthStateListener(object : AuthStateListener{
//            override fun onUserAuthenticated(user: FirebaseUser?) {
//                Log.d("Account", "Success: $user")
//                Log.d("Account", "USER: ${user?.uid}")
//            }
//
//            override fun onUserUnauthenticated() {
//                fragmentManager?.beginTransaction()
//                    ?.replace(R.id.container, Login.newInstance())
//                    ?.commit()
//                Log.d("Account", "Error:")
//            }
//
//        })
//    }
//
//    private fun attachAdapterToViewPager(){
//        val adapter = ViewPageAdapter(activity as FragmentActivity, listOfFragments)
//        binding.viewPager.adapter = adapter
//        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
//            tab.text = listOfTabs[position]
//        }.attach()
//    }
//
//    @Deprecated("Deprecated in Java", ReplaceWith(
//        "super.onCreateOptionsMenu(menu, inflater)",
//        "androidx.fragment.app.Fragment"
//    )
//    )
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_main, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//
//    }
//
//    @Deprecated("Deprecated in Java", ReplaceWith(
//        "super.onOptionsItemSelected(item)",
//        "androidx.fragment.app.Fragment"
//    )
//    )
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == R.id.menu_item) {
//            mainViewModel.logout()
//        }
//        return super.onOptionsItemSelected(item)
//
//    }
//
//
//
//
//
//
//    companion object {
//        private const val KEY_CURRENT_USER_ID = "current_user_id"
//        @JvmStatic
//        fun newInstance() = Account()
//    }
//}