package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.User

interface UserRepository {
    suspend fun onSignIn(user: User)
    fun onSignUp(user: User)
}