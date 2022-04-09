package ru.netology.nmedia.repository

import ru.netology.nmedia.api.ApiService
import ru.netology.nmedia.auth.AppAuth
import ru.netology.nmedia.dto.User
import ru.netology.nmedia.error.ApiError
import ru.netology.nmedia.error.NetworkError
import ru.netology.nmedia.error.UnknownError
import java.io.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private  val appAuth: AppAuth
): UserRepository {

    override suspend fun onSignIn(user: User) {
        try {
            val response = apiService.onSignIn(user.login, user.password)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authState = response.body() ?: throw ApiError(response.code(), response.message())
            val token = authState.token ?: throw UnknownError
            appAuth.setAuth(authState.id, token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

    override suspend fun onSignUp(login: String, pass: String, name: String) {
        try {
            val response = apiService.onSignUp(login, pass, name)
            if (!response.isSuccessful) {
                throw ApiError(response.code(), response.message())
            }
            val authState = response.body() ?: throw ApiError(response.code(), response.message())
            val token = authState.token ?: throw UnknownError
            appAuth.setAuth(authState.id, token)
        } catch (e: IOException) {
            throw NetworkError
        } catch (e: Exception) {
            throw UnknownError
        }
    }

}