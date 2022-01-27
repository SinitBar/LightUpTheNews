package com.example.lightupthenews.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArticleDatabaseDao {
    @Insert
    suspend fun insert(article: Article)

    @Query("DELETE FROM favorite_articles WHERE id = :articleId")
    suspend fun delete(articleId: Int)

    @Query("SELECT * FROM favorite_articles WHERE id = :key")
    suspend fun get(key: Int): Article?

    @Query("SELECT * FROM favorite_articles WHERE url = :articleUrl")
    suspend fun find(articleUrl: String): Article?

    @Query("DELETE FROM favorite_articles")
    suspend fun clear()

    @Query("SELECT * FROM favorite_articles")
    fun getAllArticles(): LiveData<List<Article>>

    @Query("SELECT COUNT() FROM favorite_articles")
    fun size(): Int

}