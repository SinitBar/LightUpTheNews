package com.example.lightupthenews.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false
)
abstract class ArticleDatabase: RoomDatabase() {
    abstract val articleDatabaseDao: ArticleDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: ArticleDatabase? =
            null

        fun getInstance(context: Context): ArticleDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ArticleDatabase::class.java,
                        "sleep_history_database"
                    )
                        .fallbackToDestructiveMigration() // bad, loses data when schema changes, but easy, in good should deal with Migrations
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}