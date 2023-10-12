package com.example.beintouch.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentRegistrationBinding
import com.example.beintouch.presentation.MainViewModel


class Registration : Fragment() {
    private lateinit var binding: FragmentRegistrationBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var selectemImageUri: Uri

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
        binding.selectImageButton.setOnClickListener {
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            selectemImageUri = data.data!!
            val bitmap = selectemImageUri.let {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            }
            if (bitmap != null) {
                binding.imageUserProfile.setImageBitmap(bitmap)
            }
        }
    }

    private fun registerUser() {
        binding.buttonToRegister.setOnClickListener {
            val userName = binding.inputNameSignUp.text.toString().trim()
            val email = binding.inputEmailSignUp.text.toString().trim()
            val password = binding.inputPasswordSignUp.text.toString().trim()
            if (userName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_LONG).show()
            } else {
                mainViewModel.signUpWithEmailAndPassword(
                    email,
                    password,
                    userName,
                    false,
                    userProfileImage = selectemImageUri
                )
            }
        }
    }

    private fun backToLogin() {
        binding.buttonToBackToLoginFromSignUp.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Login.newInstance())
                ?.commit()
        }
        binding.loginButton.setOnClickListener {
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