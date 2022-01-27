package com.example.lightupthenews.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.lightupthenews.articleViewModel
import com.example.lightupthenews.network.ArticleInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * an instance of [ArticleViewModel] saves articles for all users [User]
 */
class ArticleViewModel(
    val database: ArticleDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    private var articlesInDatabase = database.getAllArticles().value

    fun insertArticleToDatabaseByArticleInfo(article: ArticleInfo): Int {
        Timber.i("inserting new article...")
        val newArticle = Article(
            sourceId = article.source?.id,
            sourceName = article.source?.name,
            author = article.author,
            title = article.title,
            description = article.description,
            url = article.url,
            urlToImage = article.urlToImage,
            publishedAt = article.publishedAt.toLocaleString(),
            content = article.content
        )
        viewModelScope.launch(Dispatchers.IO) {
            database.insert(newArticle)
        }
        return newArticle.id
    }

    // used only if all users doesn't have it anymore
    fun deleteArticleOnIdFromDatabase(articleId: Int) {
        Timber.i("deleting article with id $articleId from database...")
        viewModelScope.launch(Dispatchers.IO) {
            database.delete(articleId)
        }
    }

    suspend fun clearDatabase() {
        Timber.i("cleared articles in database")
        if (!articlesInDatabase.isNullOrEmpty())
            database.clear()
    }

    suspend fun getArticleByIdFromDatabase(id: Int): Article? {
        var article: Article? = null
        if (!articlesInDatabase.isNullOrEmpty()) {
            val job = viewModelScope.launch(Dispatchers.IO) {
                article = database.get(id)
            }
            withContext(Dispatchers.IO) { job.join() }
        }
        if (article == null)
            Timber.i("article with key = $id is not found in database")
        return article
    }

    private suspend fun getArticleByArticleInfoFromDatabase(articleInfo: ArticleInfo): Article? {
        Timber.i("in getArticleByArticleInfoFromDatabase")
        if (articlesInDatabase.isNullOrEmpty()) {
            Timber.i("articlesInDatabase = $articlesInDatabase so getArticleByArticleInfoFromDatabase returns null")
            return null
        }
        var article: Article? = null
        val job = viewModelScope.launch(Dispatchers.IO) {
            Timber.i("started a job in getArticleByArticleInfoFromDatabase")
            article = database.find(articleInfo.url)
        }
        job.join()
        Timber.i("completed a job in getArticleByArticleInfoFromDatabase")
        return article
    }

    suspend fun getArticleIdInDatabaseByArticleInfo(articleInfo: ArticleInfo): Int? {
        Timber.i("in findArticleIdInArticles")
        var article: Article? = null
        val job = viewModelScope.launch(Dispatchers.IO) {
            Timber.i("started a job in findArticleIdInArticles")
            article = articleViewModel.getArticleByArticleInfoFromDatabase(articleInfo)
        }
        job.join()
        Timber.i("completed a job in findArticleIdInArticles")
        return article?.id
    }
}