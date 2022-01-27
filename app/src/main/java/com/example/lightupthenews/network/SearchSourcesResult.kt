package com.example.lightupthenews.network

data class SearchSourcesResult(
    val status: String,
    val sources: List<SourceInfo> = emptyList(),
    val code: String? = null,
    val error: String? = null
)
