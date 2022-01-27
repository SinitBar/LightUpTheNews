package com.example.lightupthenews.json

import android.content.SharedPreferences
import com.example.lightupthenews.application.sharedPreferences
import com.example.lightupthenews.user.NOT_LOGGED
import com.example.lightupthenews.user.User
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber

object JSONtoSharedPref {

    const val currentUserID = "currentUserID" // the key in SharedPreferences for current user id
    const val keys = "keys"
    private var userKeysSet = emptySet<String>().toMutableSet()
    private val json = Json

    init {
        userKeysSet =  sharedPreferences
            .getStringSet(keys, emptySet<String>())?.toMutableSet() ?: emptySet<String>().toMutableSet()
        Timber.i("initializing userKeysSet:")
        Timber.i("now userKeysSet is $userKeysSet")
    }

    fun putUserToSharedPreferences(user: User,) {
        Timber.i("put new user $user in $sharedPreferences")
        val key = buildString { append(user.id, user.loginId, user.localName, user.logged) }
        Timber.i("key for new user is $key")
        Timber.i("1) now userKeysSet = $userKeysSet")
        userKeysSet.add(key)
        Timber.i("2) now userKeysSet = $userKeysSet")
        sharedPreferences.edit().apply {
            putString(key, json.encodeToString(user)).apply()
            remove(keys).apply()
            putStringSet(keys, userKeysSet).apply()
        }
    }

    private fun decodeUserFromSharedPreferences(key: String, sp: SharedPreferences): User? {
        val userString = sp.getString(key, "")
        Timber.i("decoded user $userString from $sp")
        if (!userString.isNullOrEmpty()) {
            return json.decodeFromString(userString)
        }
        return null
    }

    fun getUsersFromSharedPreferences(sp: SharedPreferences): List<User> {
        val users = emptyList<User>().toMutableList()
        for (key in userKeysSet) {
            val user = decodeUserFromSharedPreferences(key, sp)
            user?.let {
                users.add(it)
            }
        }
        Timber.i("user list: $users")
        return users.toList()
    }

    fun getCurrentUserId() = sharedPreferences.getInt(currentUserID, NOT_LOGGED)

    fun changeCurrentUserId(id: Int) {
        sharedPreferences.edit().remove(currentUserID).apply()
        sharedPreferences.edit().putInt(currentUserID, id).apply()
        Timber.i("changed current user id to $id in $sharedPreferences")
    }
}

//        fun decodeSearchResult(resources: Resources): SearchResult {
//            val moshi = Moshi.Builder()
//                .add(KotlinJsonAdapterFactory())
//                .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
//                .build()
//
//            val inputStream = resources.openRawResource(R.raw.news_everything_bitcoin)
//            Log.i("SearchResult", "resources opened (JSONdeserialization)")
//            val jsonNewsString = inputStream.bufferedReader().readText()
//            //val newsType = object : TypeToken<SearchResult>() {}.type
//            val sRes = moshi.adapter(SearchResult::class.java).fromJson(jsonNewsString)
//            //val searchResult = gson.fromJson<SearchResult>(jsonNewsString, newsType)
//            Log.i("JSONdeserialization", "search result: $sRes")
//            return sRes ?: throw IllegalStateException("parsed into null -_-")
//        }
//
//        fun decodeSourceInfo(json: String) {
//            val type = object: TypeToken<SourceInfo>() {}.type
//            val sourceInfo = gson.fromJson<SourceInfo>(json, type)
//            Log.i("JSONdeserialization", "id = ${sourceInfo.id}, " +
//                    "name = ${sourceInfo.name}, ...")
//        }
//
//        fun decodeArticleInfo(json: String) {
//            val type = object: TypeToken<ArticleInfo>() {}.type
//            val sourceInfo = gson.fromJson<ArticleInfo>(json, type)
//            Log.i("JSONdeserialization", "id = ${sourceInfo.source}, " +
//                    "name = ${sourceInfo.author}, ..., publishedAt = ${sourceInfo.publishedAt}, ...")
//        }
//
//        fun decodeSourcesResult(json: String) {
//            val type = object: TypeToken<SearchSourcesResult>() {}.type
//            val sourceInfo = gson.fromJson<SearchSourcesResult>(json, type)
//            Log.i("JSONdeserialization", "status = ${sourceInfo.status}, " +
//                    "sources = ${sourceInfo.sources}")
//        }
