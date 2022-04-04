package ru.netology.nmedia.activity.Dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.util.AndroidUtils

import ru.netology.nmedia.viewmodel.AuthViewModel


class AuthenticationFragment : DialogFragment() {

    private val viewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentAuthenticationBinding.inflate(inflater, container, false)

        val enter = binding.enter

        enter.setOnClickListener {
            val usernameEditText = binding.username.text.toString()
            val passwordEditText = binding.password.text.toString()
            if (usernameEditText=="" || passwordEditText==""){
                Snackbar.make(binding.root, R.string.All_fields, Snackbar.LENGTH_SHORT).show()
            } else {
                val user = User(usernameEditText, passwordEditText)
                viewModel.onSignIn(user)
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            }
        }

        return binding.root

    }




}