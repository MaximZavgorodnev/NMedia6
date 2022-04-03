package ru.netology.nmedia.repository

import ru.netology.nmedia.dto.User

interface UserRepository {
    fun onSignIn(user: User)
    fun onSignUp(user: User)
    fun onSignOut()
}