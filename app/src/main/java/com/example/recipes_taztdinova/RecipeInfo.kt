package com.example.recipes_taztdinova

data class RecipeInfo(
    val id: Int,
    val title: String,
    val readyInMinutes: Int,
    val servings: Int,
    val extendedIngredients: List<Ingredient>,
    val analyzedInstructions: List<InstructionBlock>
)

data class Ingredient(val original: String)

data class InstructionBlock(val steps: List<Step>)

data class Step(val number: Int, val step: String)
