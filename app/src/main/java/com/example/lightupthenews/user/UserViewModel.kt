package com.example.lightupthenews.user

import android.app.Application
import androidx.lifecycle.*
import com.example.lightupthenews.articleViewModel
import com.example.lightupthenews.json.JSONtoSharedPref
import com.example.lightupthenews.network.ArticleInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.lang.IllegalArgumentException

const val NOT_LOGGED = -1

class UserViewModel(
    application: Application
) : AndroidViewModel(application) {

    var doNotLogIn = false

    var currentArticle: ArticleInfo? = null

    private var _currentUserId: Int

    val currentUserId: Int
        get() = _currentUserId

    init {
        Timber.i("created")
        _currentUserId = JSONtoSharedPref.getCurrentUserId()
    }

    fun logIn(id: Int) {
        require(id >= 0)
        _currentUserId = id
        JSONtoSharedPref.changeCurrentUserId(id)
    }

    fun logOut() {
        _currentUserId = NOT_LOGGED
        JSONtoSharedPref.changeCurrentUserId(NOT_LOGGED)
    }

    /**
     * [deleteArticleFromUser] deletes article with articleInfo from user
     * and if there is no another user having this articles in favorites
     * article is being deleted from database
     * throws IllegalArgumentException if article doesn't exist in user's favorites
     */
    suspend fun deleteArticleFromUser(user: User, articleInfo: ArticleInfo) {
        var articleId: Int = -1
        val job = viewModelScope.launch(Dispatchers.IO) {
            articleViewModel.getArticleIdInDatabaseByArticleInfo(articleInfo)?.let {
                articleId = it
            }
        }
        job.join()
        if (!user.favoriteArticles.remove(articleId))
            throw IllegalArgumentException("trying to remove article with id $articleId that doesn't exist in $user")
        Timber.i("article $articleInfo is removed from user $user")
        for (anyUser in User.getUsers()) {
            if (anyUser.favoriteArticles.contains(articleId))
                return
        }
        articleViewModel.deleteArticleOnIdFromDatabase(articleId)
    }

    suspend fun addArticleToUser(user: User, articleInfo: ArticleInfo) {
        var articleId: Int? = -1
        val job = viewModelScope.launch {
            articleId = articleViewModel.getArticleIdInDatabaseByArticleInfo(articleInfo)
            if (articleId == null) {
                articleId = articleViewModel.insertArticleToDatabaseByArticleInfo(articleInfo)
            }
        }
        job.join()
        if (user.favoriteArticles.contains(articleId)) {
            Timber.i("seems like user $user already has article $articleInfo")
        } else articleId?.let { user.favoriteArticles.add(it) }
    }

    fun doesUserHaveArticle(user: User, articleInfo: ArticleInfo): Boolean {
        Timber.i("in doesUserHaveArticle")
        var articleId: Int = -1
        val job = viewModelScope.launch(Dispatchers.IO) {
            Timber.i("started a job in doesUserHaveArticle")
            articleViewModel.getArticleIdInDatabaseByArticleInfo(articleInfo)?.let {
                articleId = it
            }
        }
        runBlocking { job.join() }
        Timber.i("completed a job in doesUserHaveArticle")
        return user.favoriteArticles.contains(articleId)
    }

}