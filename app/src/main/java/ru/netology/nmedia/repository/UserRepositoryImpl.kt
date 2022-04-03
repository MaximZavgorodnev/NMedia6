package ru.netology.nmedia.repository

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.auth.AuthState
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class UserRepositoryImpl(): UserRepository {
//    var dataUser = User(
//        login = "",
//        password = "")

//    val appAuth = AppAuth(context)
    override suspend fun onSignIn(user: User) {
        try {
            val response = PostsApi.service.onSignIn(user.login, user.password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authState = response.body()


//            AppAuth.setAuth(authState.id, authState.token)

//            instance = response.body()


        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override fun onSignUp(user: User) {
        TODO("Not yet implemented")
    }

    override fun onSignOut() {
        TODO("Not yet implemented")
    }
}