package com.example.recipes_taztdinova

data class SearchResult(val results: List<Recipe>)

data class Recipe(
    val id: Int,
    val title: String,
    val readyInMinutes: Int,
    val servings: Int
)
