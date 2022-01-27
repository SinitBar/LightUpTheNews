package com.example.lightupthenews.network

import kotlinx.serialization.Serializable

@Serializable
data class SourceInfo(
    val id: String?,
    val name: String?,
)