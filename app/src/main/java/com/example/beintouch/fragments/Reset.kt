package com.example.beintouch.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentResetBinding
import com.example.beintouch.presentation.MainViewModel


class Reset : Fragment() {
    private lateinit var binding: FragmentResetBinding
    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonToBackToLoginFromReset.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Login.newInstance())
                ?.commit()
        }
        resetPassword()
    }

    private fun resetPassword() {
        binding.buttonToReset.setOnClickListener {
            val email = binding.inputEmailReset.text.toString().trim()
            if (email.isEmpty()) {
                binding.emailReset.boxStrokeColor = resources.getColor(R.color.redOffline)
                binding.inputEmailReset.requestFocus()
                Toast.makeText(requireContext(), "Введите ваш email!", Toast.LENGTH_SHORT).show()
            } else {
                mainViewModel.resetPassword(email)
                Toast.makeText(requireContext(), "Отправлено!", Toast.LENGTH_SHORT).show()
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = Reset()
    }
}