package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentLoginBinding
import com.example.beintouch.presentation.AuthStateListener
import com.example.beintouch.presentation.MainViewModel
import com.google.firebase.auth.FirebaseUser


class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var userEmail: String
    private lateinit var userPassword: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchSignUp()
        launchReset()
        checkUserForLogin()
        observeViewModel()
        binding.buttonToLogin.visibility = View.VISIBLE
        binding.loaderLoginButton.visibility = View.GONE

    }

    private fun checkUserForLogin() {
        binding.buttonToLogin.setOnClickListener {
            userEmail = binding.emailInputLogin.text.toString().trim()
            userPassword = binding.passwordInputLogin.text.toString().trim()
            if (userEmail.isEmpty() || userPassword.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_LONG).show()
            } else {
                binding.buttonToLogin.visibility = View.GONE
                binding.loaderLoginButton.visibility = View.VISIBLE
                mainViewModel.signInWithEmailAndPassword(userEmail, userPassword)
            }
        }

    }

    private fun observeViewModel() {
        mainViewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(
                    requireContext(),
                    "Неправильно введен email или password!",
                    Toast.LENGTH_LONG
                ).show()
            }

        }

        mainViewModel.setAuthStateListener(object : AuthStateListener {
            override fun onUserAuthenticated(user: FirebaseUser?) {
                if (user != null) {
                    Log.d("Login", "USER: ${user.uid}")
                    fragmentManager?.beginTransaction()
                        ?.replace(R.id.container, Chats.newInstance(user.uid))
                        ?.commit()
                }

            }

            override fun onUserUnauthenticated() {
                Log.d("Login", "Unauthorized")
            }

        })
    }


    private fun launchSignUp() {
        binding.buttonToSignUp.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Registration.newInstance())
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    private fun launchReset() {
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