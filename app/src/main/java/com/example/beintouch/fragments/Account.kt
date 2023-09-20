package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentAccountBinding
import com.example.beintouch.databinding.FragmentLoginBinding


class Account : Fragment() {
    private lateinit var binding: FragmentAccountBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }






    companion object {
        @JvmStatic
        fun newInstance() = Account()
    }
}