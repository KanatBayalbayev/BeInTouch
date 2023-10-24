package com.example.beintouch.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentRegistrationBinding
import com.example.beintouch.presentation.MainViewModel
import java.io.File
import java.io.FileOutputStream


class Registration : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private val mainViewModel: MainViewModel by activityViewModels()


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
        binding.buttonToRegister.visibility = View.VISIBLE
        binding.loaderLoginButton.visibility = View.GONE
        binding.buttonToRegister.setOnClickListener {
            val userName = binding.inputNameSignUp.text.toString().trim()
            val email = binding.inputEmailSignUp.text.toString().trim()
            val password = binding.inputPasswordSignUp.text.toString().trim()

            if (password.isBlank()) {
                binding.tilPasswordReg.boxStrokeColor = resources.getColor(R.color.redOffline)
                binding.inputPasswordSignUp.requestFocus()
            }
            if (email.isBlank()) {
                binding.tilEmailReg.boxStrokeColor = resources.getColor(R.color.redOffline)
                binding.inputEmailSignUp.requestFocus()
            }
            if (userName.isBlank()) {
                binding.tilUsername.boxStrokeColor = resources.getColor(R.color.redOffline)
                binding.inputNameSignUp.requestFocus()
            }

            if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_LONG).show()

            } else {
                binding.buttonToRegister.visibility = View.GONE
                binding.loaderLoginButton.visibility = View.VISIBLE
                mainViewModel.signUpWithEmailAndPassword(
                    email,
                    password,
                    userName,
                    )
            }
        }

        mainViewModel.isErrorReg.observe(viewLifecycleOwner){
            if (it){
                Toast.makeText(
                    requireContext(),
                    "Неправильно введен email!",
                    Toast.LENGTH_SHORT
                ).show()
                binding.buttonToRegister.visibility = View.VISIBLE
                binding.loaderLoginButton.visibility = View.GONE
            }
        }
    }


    private fun backToLogin() {
        binding.buttonToBackToLoginFromSignUp.setOnClickListener {
            navigateToLoginFragment()
        }
        binding.loginButton.setOnClickListener {
            navigateToLoginFragment()
        }
    }
    private fun navigateToLoginFragment() {
        fragmentManager?.beginTransaction()
            ?.replace(R.id.container, Login.newInstance())
            ?.commit()
    }


    companion object {
        @JvmStatic
        fun newInstance() = Registration()
    }
}