package com.example.beintouch.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentLoginBinding
import com.example.beintouch.presentation.AuthStateListener
import com.example.beintouch.presentation.LoginViewModel
import com.google.firebase.auth.FirebaseUser


class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val loginViewModel: LoginViewModel by activityViewModels()


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
        observeLoginVM()

    }

    private fun checkUserForLogin() {
        binding.buttonToLogin.setOnClickListener {
            val userEmail = binding.emailInputLogin.text.toString().trim()
            val userPassword = binding.passwordInputLogin.text.toString().trim()
            loginViewModel.signInWithEmailAndPassword(userEmail, userPassword)
        }
    }

    private fun observeLoginVM() {
        loginViewModel.error.observe(viewLifecycleOwner) {
            if (it != null) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

        }
//        loginViewModel.isExistedUser.observe(viewLifecycleOwner) {
//            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
//            if (it != null) {
//                fragmentManager?.beginTransaction()
//                    ?.replace(R.id.container, Account.newInstance())
//                    ?.commit()
//
//            }
//        }

        loginViewModel.setAuthStateListener(object : AuthStateListener{
            override fun onUserAuthenticated(user: FirebaseUser?) {
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.container, Account.newInstance())
                    ?.commit()
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