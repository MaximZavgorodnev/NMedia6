package ru.netology.nmedia.activity.Dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentRegistrationBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthViewModel

@AndroidEntryPoint
class RegistrationFragment : DialogFragment() {
    private val viewModel: AuthViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRegistrationBinding.inflate(inflater, container, false)

        val enter = binding.enter

        enter.setOnClickListener {
            val usernameEditText = binding.username.text.toString()
            val loginEditText = binding.login.text.toString()
            val passwordEditText = binding.password.text.toString()
            val repeatPasswordEditText = binding.repeatPassword.text.toString()
            if (passwordEditText != repeatPasswordEditText){
                Snackbar.make(binding.root, R.string.password_not_match, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                if (loginEditText == "" || passwordEditText == "") {
                    Snackbar.make(binding.root, R.string.All_fields, Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {

                    viewModel.onSignUp(loginEditText, passwordEditText, usernameEditText)
                    AndroidUtils.hideKeyboard(requireView())
                    findNavController().navigateUp()
                }
            }
        }

        return binding.root
    }


}