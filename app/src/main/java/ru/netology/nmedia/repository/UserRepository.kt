package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.User

interface UserRepository {
    suspend fun onSignIn(user: User)
    suspend fun onSignUp(login: String, pass: String, name: String)
}