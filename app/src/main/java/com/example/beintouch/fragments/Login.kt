package com.example.beintouch.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import android.text.method.PasswordTransformationMethod
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
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        sharedPreferences =
            requireActivity().getSharedPreferences("myPreferences", Context.MODE_PRIVATE)

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        launchSignUp()
        launchReset()
        checkUserForLogin()
        observeViewModel()
        saveUserData()
        binding.buttonToLogin.visibility = View.VISIBLE
        binding.loaderLoginButton.visibility = View.GONE

        val email = sharedPreferences.getString("email", "")
        val password = sharedPreferences.getString("password", "")
        val isChecked = sharedPreferences.getBoolean("isChecked", false)
        binding.emailInputLogin.setText(email)
        binding.passwordInputLogin.setText(password)
        binding.checkboxRemember.isChecked = isChecked

    }

    private fun checkUserForLogin() {
        binding.buttonToLogin.setOnClickListener {
            userEmail = binding.emailInputLogin.text.toString().trim()
            userPassword = binding.passwordInputLogin.text.toString().trim()
            if (userPassword.isBlank()) {
                binding.tilPassword.boxStrokeColor = resources.getColor(R.color.redOffline)
                binding.passwordInputLogin.requestFocus()
            }
            if (userEmail.isBlank()) {
                binding.tilEmail.boxStrokeColor = resources.getColor(R.color.redOffline)
                binding.emailInputLogin.requestFocus()
            }

            if (userEmail.isEmpty() || userPassword.isEmpty()
            ) {
                Toast.makeText(requireContext(), "Заполните все поля!", Toast.LENGTH_LONG).show()
            } else {
                binding.loaderLoginButton.visibility = View.VISIBLE
                binding.buttonToLogin.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Выполняется вход!!",
                    Toast.LENGTH_SHORT
                ).show()
                mainViewModel.signInWithEmailAndPassword(userEmail, userPassword)

            }
            if (binding.checkboxRemember.isChecked){
                if (userEmail.isNotEmpty() && userPassword.isNotEmpty()) {
                    val editor = sharedPreferences.edit()
                    editor.putString("email", userEmail)
                    editor.putString("password", userPassword)
                    editor.putBoolean("isChecked", true)
                    editor.apply()

                }
            }
            mainViewModel.isError.observe(viewLifecycleOwner) {
                if (it) {
                    Toast.makeText(
                        requireContext(),
                        "Неправильный логин или пароль!",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.buttonToLogin.visibility = View.VISIBLE
                    binding.loaderLoginButton.visibility = View.GONE
                }
            }
            mainViewModel.noConnection.observe(viewLifecycleOwner){
                if (it){
                    fragmentManager?.beginTransaction()
                        ?.replace(R.id.container, NoConnection.newInstance())
                        ?.commit()
                }
            }

        }

        binding.buttonClosePassword.setOnClickListener {
            binding.passwordInputLogin.transformationMethod = null
            binding.buttonOpenPassword.visibility = View.VISIBLE
            binding.buttonClosePassword.visibility = View.GONE
        }
        binding.buttonOpenPassword.setOnClickListener {
            binding.passwordInputLogin.transformationMethod = PasswordTransformationMethod.getInstance()
            binding.buttonOpenPassword.visibility = View.GONE
            binding.buttonClosePassword.visibility = View.VISIBLE
        }

    }

    private fun saveUserData() {



    }


    private fun observeViewModel() {
//        mainViewModel.error.observe(viewLifecycleOwner) {
//            if (it != null) {
//                Toast.makeText(
//                    requireContext(),
//                    "Неправильно введен email или password!",
//                    Toast.LENGTH_LONG
//                ).show()
//                binding.loaderLoginButton.visibility = View.GONE
//            }
//
//        }

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