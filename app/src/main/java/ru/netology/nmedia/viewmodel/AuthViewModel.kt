package ru.netology.nmedia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.error.UnknownError
import ru.netology.nmedia.repository.UserRepository
import ru.netology.nmedia.repository.UserRepositoryImpl
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private  val appAuth: AppAuth,
    private val repositoryUser : UserRepository
) : ViewModel() {
    val data: LiveData<AuthState> = appAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = appAuth.authStateFlow.value.id != 0L




    fun onSignIn(user: User){
        viewModelScope.launch {
            try {
                repositoryUser.onSignIn(user)

            } catch (e: Exception) {
                throw UnknownError
            }

        }
    }

    fun onSignUp(loginEditText: String, passwordEditText: String, usernameEditText: String){
        viewModelScope.launch {
            try {
                repositoryUser.onSignUp(loginEditText, passwordEditText, usernameEditText)
            } catch (e: Exception) {
                throw UnknownError
            }

        }
    }

}