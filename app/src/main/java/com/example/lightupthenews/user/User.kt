package com.example.lightupthenews.user

import com.example.lightupthenews.application.sharedPreferences
import com.example.lightupthenews.application.userViewModel
import com.example.lightupthenews.articleViewModel
import com.example.lightupthenews.json.JSONtoSharedPref
import com.example.lightupthenews.network.ArticleInfo
import kotlinx.serialization.Serializable
import timber.log.Timber
import java.lang.IllegalArgumentException

enum class UserStatusLogged {
    IN_FACEBOOK,
    IN_VK,
}

@Serializable
data class User(
    val id: Int = number++,
    val loginId: String, // LoginResult.accessToken.userId
    var localName: String = "User$id",
    val logged: UserStatusLogged,
    var imageUrl: String? = null,
    val favoriteArticles: MutableList<Int> = emptyList<Int>().toMutableList()
) {

    companion object {
        private var number = 0
        private val users = mutableListOf<User>()

        init {
            Timber.i("class User is created")
            users.clear()
            users.addAll(JSONtoSharedPref.getUsersFromSharedPreferences(sharedPreferences))
            number = users.size
            Timber.i("users now: $users")
        }

        fun addOrLoginUser(newUser: User) {
            val existingId = isAlreadyExist(newUser.loginId)
            if (existingId == NOT_LOGGED) {
                users.add(newUser)
                JSONtoSharedPref.putUserToSharedPreferences(newUser)
                userViewModel.logIn(newUser.id)
                Timber.i("added a new user: $newUser")
            } else {
                get(existingId).apply {
                    imageUrl = newUser.imageUrl
                    localName = newUser.localName
                }
                number--
                userViewModel.logIn(existingId)
                Timber.i("logged in as ${get(existingId)}")
            }
        }

        fun getUsers() = users.toList()

        fun get(userId: Int): User {
            for (user in getUsers()) {
                if (user.id == userId)
                    return user
            }
            throw IllegalArgumentException("trying to get not-existing user")
        }

        private fun isAlreadyExist(loginId: String): Int {
            for (user in getUsers()) {
                if (user.loginId == loginId)
                    return user.id
            }
            return NOT_LOGGED
        }
    }
}