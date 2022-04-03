package ru.netology.nmedia.repository

import kotlinx.coroutines.flow.Flow
import ru.netology.nmedia.dto.User

class UserRepositoryImpl(): UserRepository {
    val dataUser = User(
        login = "",
        password = "")
    override fun onSignIn(user: User) {
        TODO("Not yet implemented")
    }

    override fun onSignUp(user: User) {
        TODO("Not yet implemented")
    }

    override fun onSignOut() {
        TODO("Not yet implemented")
    }
}