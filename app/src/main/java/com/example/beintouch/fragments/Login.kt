package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentLoginBinding

class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonToSignUp.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Registration.newInstance())
                ?.addToBackStack(null)
                ?.commit()
        }
        binding.buttonToOpenReset.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Reset.newInstance())
                ?.addToBackStack(null)
                ?.commit()
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = Login()
    }
}