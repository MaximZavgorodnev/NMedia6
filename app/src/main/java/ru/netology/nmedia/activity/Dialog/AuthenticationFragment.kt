package ru.netology.nmedia.activity.Dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.util.AndroidUtils

import ru.netology.nmedia.viewmodel.AuthViewModel


class AuthenticationFragment : DialogFragment() {

//    private lateinit var loginViewModel: LoginViewModel
//    private var _binding: FragmentAuthenticationBinding? = null
//
//
//    private val binding get() = _binding!!

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
            val user = User(usernameEditText, passwordEditText)
            viewModel.onSignIn(user)
            AndroidUtils.hideKeyboard(requireView())
            findNavController().navigateUp()
        }

        return binding.root

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



}