package com.example.recipes_taztdinova

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RecipeDao {

    @Query("SELECT * FROM recipes WHERE userName = :userName")
    fun getRecipes(userName: String): List<RecipeEntity>

    @Insert
    fun addRecipe(recipe: RecipeEntity)

    @Update
    fun updateRecipe(recipe: RecipeEntity)

    @Delete
    fun deleteRecipe(recipe: RecipeEntity)
}
