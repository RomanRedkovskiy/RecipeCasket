package com.example.recipecasket.model

data class UserAdditionalData(
    @JvmField var userId: String? = null,
    @JvmField var firstName: String? = null,
    @JvmField var lastName: String? = null,
    @JvmField var description: String? = null,
    @JvmField var registrationDate: String? = null,
    @JvmField var country: String? = null,
    @JvmField var cookingExperience: Int? = null,
    @JvmField var cookingPreference: String? = null,
    @JvmField var starredRecipes: List<String>? = null,
)