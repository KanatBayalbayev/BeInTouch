package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentRegistrationBinding
import com.example.beintouch.presentation.MainViewModel


class Registration : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private val loginViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToLogin()
        registerUser()
    }

    private fun registerUser() {
        binding.buttonToRegister.setOnClickListener {
            val userName = binding.inputNameSignUp.text.toString().trim()
            val email = binding.inputEmailSignUp.text.toString().trim()
            val password = binding.inputPasswordSignUp.text.toString().trim()

            loginViewModel.signUpWithEmailAndPassword(email, password, userName)
        }
    }

    private fun backToLogin() {
        binding.buttonToBackToLoginFromSignUp.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Login.newInstance())
                ?.commit()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = Registration()
    }
}