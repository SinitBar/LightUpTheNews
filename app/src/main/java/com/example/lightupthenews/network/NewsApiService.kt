package com.example.lightupthenews.network

import androidx.lifecycle.LiveData
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.*
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities

private const val BASE_URL = "https://newsapi.org/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .add(Date::class.java, Rfc3339DateJsonAdapter().nullSafe())
    .build()

private val retrofit = Retrofit.Builder() //  create a Retrofit object
    .addConverterFactory(MoshiConverterFactory.create(moshi))  // maybe should be GsonConverterFactory.create() // add in dependencies
    .baseUrl(BASE_URL)
    .build()

var LANGAUGE = Locale.getDefault().language

interface NewsApiService {

    @Headers("X-Api-Key: 0065d762c2fb4a5d9ea434c67b04b152")
    @GET("v2/everything") // https://newsapi.org/ v2/everything?q=bitcoin&apiKey=0065d762c2fb4a5d9ea434c67b04b152
    suspend fun getAllNews(
        @Query("q") q: String, // Keywords or phrases to search for in the article title and body. The complete value for q must be URL-encoded. Max length: 500 chars. +++ advanced
        @Query("qInTitle") qInTitle: String? = null, // Keywords or phrases to search for in the article title only. The complete value for qInTitle must be URL-encoded. Max length: 500 chars +++ advanced
        @Query("sources") sources: String? = null, // A comma-separated string of identifiers (maximum 20) for the news sources or blogs you want headlines from. Use the /sources endpoint to locate these programmatically or look at the sources index.
        @Query("domains") domains: String? = null, // A comma-seperated string of domains (eg bbc.co.uk, techcrunch.com, engadget.com) to restrict the search to.
        @Query("excludeDomains") excludeDomains: String? = null, // A comma-seperated string of domains (eg bbc.co.uk, techcrunch.com, engadget.com) to remove from the results.
        @Query("from") from: Date? = null, // A date and optional time for the oldest article allowed. This should be in ISO 8601 format (e.g. 2022-01-22 or 2022-01-22T09:50:57) Default: the oldest according to your plan.
        @Query("to") to: Date? = null, // A date and optional time for the newest article allowed. This should be in ISO 8601 format (e.g. 2022-01-22 or 2022-01-22T09:50:57) Default: the newest according to your plan.
        @Query("language") language: String = LANGAUGE, // The 2-letter ISO-639-1 code of the language you want to get headlines for. Possible options: ardeenesfrheitnlnoptruseudzh. Default: all languages returned.
        @Query("sortBy") sortBy: String = "publishedAt", // The order to sort the articles in. Possible options: relevancy, popularity, publishedAt. relevancy = articles more closely related to q come first. popularity = articles from popular sources and publishers come first. publishedAt = newest articles come first. Default: publishedAt
        @Query("pageSize") pageSize: Int = 100, // The number of results to return per page. Default: 100. Maximum: 100.
        @Query("page") page: Int = 1, // Use this to page through the results. Default: 1.
    ): Response<SearchResult?>

//    @GET("v2/top-headlines?country=us&apiKey=0065d762c2fb4a5d9ea434c67b04b152") // https://newsapi.org/v2/top-headlines?country=us&apiKey=0065d762c2fb4a5d9ea434c67b04b152
//    suspend fun getTopHeadlines(): SearchResult
//
//    @GET("v2/top-headlines/sources?apiKey=0065d762c2fb4a5d9ea434c67b04b152") // https://newsapi.org/v2/top-headlines/sources?apiKey=API_KEY
//    suspend fun getSources(): SearchSourcesResult
//

}

object NewsApi {
    val retrofitService : NewsApiService by lazy {
        retrofit.create(NewsApiService::class.java) }
}
