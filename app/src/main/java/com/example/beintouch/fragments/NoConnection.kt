package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentLoginBinding
import com.example.beintouch.databinding.FragmentNoConnectionBinding


class NoConnection : Fragment() {
    private lateinit var binding: FragmentNoConnectionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoConnectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonToTryAgain.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Login.newInstance())
                ?.commit()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoConnection()
    }
}