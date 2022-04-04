package ru.netology.nmedia.repository

import ru.netology.nmedia.api.PostsApi
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException

class UserRepositoryImpl: UserRepository {

    override suspend fun onSignIn(user: User) {
        try {
            val response = PostsApi.service.onSignIn(user.login, user.password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authState = response.body() ?: throw ApiError(response.code(), response.message())
            val token = authState.token ?: throw UnknownError
            AppAuth.getInstance().setAuth(authState.id, token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun onSignUp(login: String, pass: String, name: String) {
        try {
            val response = PostsApi.service.onSignUp(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authState = response.body() ?: throw ApiError(response.code(), response.message())
            val token = authState.token ?: throw UnknownError
            AppAuth.getInstance().setAuth(authState.id, token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}