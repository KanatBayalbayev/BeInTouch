package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.adapters.ViewPageAdapter
import com.example.beintouch.databinding.FragmentAccountBinding
import com.example.beintouch.presentation.AuthStateListener
import com.example.beintouch.presentation.MainViewModel
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseUser


class Account : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val loginViewModel: MainViewModel by activityViewModels()

    private val listOfFragments = listOf(
        Chats.newInstance(),
        Users.newInstance()
    )
    private val listOfTabs = listOf(
        "Chats",
        "Users"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
        attachAdapterToViewPager()

    }

    private fun observeViewModel(){
        loginViewModel.setAuthStateListener(object : AuthStateListener{
            override fun onUserAuthenticated(user: FirebaseUser?) {
                Log.d("Account", "Success: $user")
            }

            override fun onUserUnauthenticated() {
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.container, Login.newInstance())
                    ?.commit()
            }

        })
    }

    private fun attachAdapterToViewPager(){
        val adapter = ViewPageAdapter(activity as FragmentActivity, listOfFragments)
        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = listOfTabs[position]
        }.attach()
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onCreateOptionsMenu(menu, inflater)",
        "androidx.fragment.app.Fragment"
    )
    )
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onOptionsItemSelected(item)",
        "androidx.fragment.app.Fragment"
    )
    )
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item) {
            loginViewModel.logout()
        }
        return super.onOptionsItemSelected(item)

    }






    companion object {
        @JvmStatic
        fun newInstance() = Account()
    }
}