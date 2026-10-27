package com.example.recipes_taztdinova

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userName: String,
    val title: String,
    val minutes: Int,
    val servings: Int,
    val ingredients: String,
    val steps: String,
    val comment: String
)
