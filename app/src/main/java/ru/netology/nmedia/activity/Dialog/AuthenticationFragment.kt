package ru.netology.nmedia.activity.Dialog


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import ru.netology.nmedia.databinding.FragmentAuthenticationBinding
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
        val usernameEditText = binding.username
        val passwordEditText = binding.password
        val enter = binding.enter

        enter.setOnClickListener{

        }

        return binding.root

    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {



}