package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentChatBinding
import com.example.beintouch.databinding.FragmentChatsBinding
import com.example.beintouch.databinding.FragmentLoginBinding
import com.example.beintouch.presentation.MainViewModel


class Chat : Fragment() {
    private lateinit var binding: FragmentChatBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }





    companion object {
        @JvmStatic
        fun newInstance() = Chat()
    }
}