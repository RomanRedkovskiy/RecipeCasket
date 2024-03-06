package com.example.recipecasket.model

import android.view.View
import android.widget.EditText
import com.example.recipecasket.R

class UserGUI(private val rootView: View) {

    val profileNameEditText: EditText = rootView.findViewById(R.id.etProfileName)
    val passwordEditText: EditText = rootView.findViewById(R.id.etPassword)
    val firstNameEditText: EditText = rootView.findViewById(R.id.etFirstName)
    val lastNameEditText: EditText = rootView.findViewById(R.id.etLastName)
    val descriptionEditText: EditText = rootView.findViewById(R.id.etProfileDescription)
    val countryEditText: EditText = rootView.findViewById(R.id.etCountry)
    val cookingExperienceEditText: EditText = rootView.findViewById(R.id.etCookingExperience)
    val cookingPreferenceEditText: EditText = rootView.findViewById(R.id.etCookingPreference)

}