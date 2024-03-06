package com.example.recipecasket.model

import java.util.Date
data class User(
    @JvmField var userId: String? = null,
    @JvmField var profileName: String? = null,
    @JvmField var password: String? = null,
    @JvmField var firstName: String? = null,
    @JvmField var lastName: String? = null,
    @JvmField var description: String? = null,
    @JvmField var registrationDate: String? = null,
    @JvmField var country: String? = null,
    @JvmField var cookingExperience: Int? = null,
    @JvmField var cookingPreference: String? = null,
    @JvmField var numberOfStarredRecipes: Int? = null,
    @JvmField var numberOfReviews: Int? = null
)