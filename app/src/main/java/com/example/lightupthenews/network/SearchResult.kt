package com.example.lightupthenews.network

data class SearchResult(
    var status: String,
    var totalResults: Int = 0,
    var articles: List<ArticleInfo>,
)