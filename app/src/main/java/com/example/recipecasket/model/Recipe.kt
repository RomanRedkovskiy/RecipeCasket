package com.example.recipecasket.model

data class Recipe(
    @JvmField var recipeId: String? = null,
    @JvmField var name: String? = null,
    @JvmField var cuisine: String? = null,
    @JvmField var ingredients: String? = null,
    @JvmField var recipe: String? = null,
    @JvmField var cookingTime: Int? = null,
)