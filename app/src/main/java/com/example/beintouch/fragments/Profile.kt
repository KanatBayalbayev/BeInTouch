package com.example.beintouch.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.beintouch.R
import com.example.beintouch.databinding.FragmentProfileBinding
import com.example.beintouch.presentation.AuthStateListener
import com.example.beintouch.presentation.MainViewModel
import com.example.beintouch.presentation.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso


class Profile : Fragment() {
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var binding: FragmentProfileBinding
    private lateinit var currentUserID: String
    private lateinit var selectedImageUri: Uri


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        currentUserID = arguments?.getString(KEY_CURRENT_USER_ID).toString()
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backToChatsFromChat()
        newDataForCurrentUser()
        binding.buttonUserIconProfileUpload.setOnClickListener {
            openGallery()
        }
        profileViewModel.getInfoAboutUser(currentUserID)
        profileViewModel.user.observe(viewLifecycleOwner) {
            if (it.userProfileImage != ""){
                Picasso.get().load(it.userProfileImage).into(binding.userIconProfileCircleImageView)
                binding.userIconProfileCircleImageView.visibility = View.VISIBLE
                binding.userIconProfileImageView.visibility = View.GONE
            } else {
                binding.userIconProfileCircleImageView.visibility = View.GONE
                binding.userIconProfileImageView.visibility = View.VISIBLE
            }

            binding.userName.setText(it.name)
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
            selectedImageUri = data.data!!
            val bitmap = selectedImageUri.let {
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, it)
            }
            if (bitmap != null) {
                binding.userIconProfileCircleImageView.setImageBitmap(bitmap)
            }
        }
    }





    private fun newDataForCurrentUser() {
        binding.buttonToSaveNewData.setOnClickListener {
            val newUserName = binding.userName.text.toString().trim()

            if (newUserName.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Enter your username",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }
            profileViewModel.changeUserData(currentUserID, newUserName, selectedImageUri)
            profileViewModel.logout()
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Login.newInstance())
                ?.commit()
        }
    }


    private fun backToChatsFromChat() {
        binding.buttonToBackToChatsFromProfile.setOnClickListener {
            fragmentManager?.beginTransaction()
                ?.replace(R.id.container, Chats.newInstance(currentUserID))
                ?.commit()
        }
    }


    companion object {
        private const val KEY_CURRENT_USER_ID = "current_user_id"

        @JvmStatic
        fun newInstance(parameterUserID: String): Profile {
            val fragment = Profile()
            val args = Bundle()
            args.putString(KEY_CURRENT_USER_ID, parameterUserID)
            fragment.arguments = args
            return fragment
        }
    }
}