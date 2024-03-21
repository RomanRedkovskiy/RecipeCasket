package com.example.recipecasket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipecasket.R
import com.example.recipecasket.model.Recipe
import com.example.recipecasket.model.UserAdditionalData
import com.example.recipecasket.model.UserGUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrationActivity : AppCompatActivity() {

    private lateinit var userGUI: UserGUI
    private lateinit var btnRegistration: Button

    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var dbRecipeRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userGUI = UserGUI(findViewById(android.R.id.content))
        btnRegistration = findViewById(R.id.btnRegistration)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        btnRegistration.setOnClickListener {
            saveUserData()
        }

           // saveRecipe()
    }

    private fun saveUserData() {
        val email = userGUI.emailEditText.text.toString()
        val password = userGUI.passwordEditText.text.toString()
        val firstName = userGUI.firstNameEditText.text.toString()
        val lastName = userGUI.lastNameEditText.text.toString()
        val description = userGUI.descriptionEditText.text.toString()
        val country = userGUI.countryEditText.text.toString()
        val cookingExperience = userGUI.cookingExperienceEditText.text.toString().toIntOrNull()
        val cookingPreference = userGUI.cookingPreferenceEditText.text.toString()

        if (email.isEmpty()) {
            userGUI.emailEditText.error = "Please enter email"
            return
        }
        if (password.isEmpty()) {
            userGUI.passwordEditText.error = "Please enter password"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userAdditionalData = UserAdditionalData(
                            userId = user.uid,
                            firstName = firstName,
                            lastName = lastName,
                            description = description,
                            registrationDate = SimpleDateFormat(
                                "yyyy-MM-dd", Locale.getDefault()
                            ).format(Date()),
                            country = country,
                            cookingExperience = cookingExperience,
                            cookingPreference = cookingPreference,
                            starredRecipes = mutableListOf(),
                        )
                        saveAdditionalDataInDatabase(userAdditionalData)

                    }
                } else {
                    val exception = task.exception
                    // Handle the user creation failure
                    if (exception is FirebaseAuthException) {
                        val errorCode = exception.errorCode

                        // Handle specific error codes or display the error message
                        when (errorCode) {
                            "ERROR_WEAK_PASSWORD" -> {
                                userGUI.passwordEditText.error = "Password is too weak"
                            }

                            "ERROR_INVALID_EMAIL" -> {
                                userGUI.emailEditText.error = "Email is invalid"
                            }

                            "ERROR_EMAIL_ALREADY_IN_USE" -> {
                                userGUI.emailEditText.error = "Email already exists"
                            }
                            else -> {
                                userGUI.emailEditText.error = "Try another email"
                                userGUI.passwordEditText.error = "Try another password"
                            }
                        }
                    } else {
                        userGUI.emailEditText.error = "Try another email"
                        userGUI.passwordEditText.error = "Try another password"
                    }
                }
            }
    }

    private fun saveAdditionalDataInDatabase(userData: UserAdditionalData) {
        dbRef.child(userData.userId!!).setValue(userData)
            .addOnCompleteListener {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Register successful", Toast.LENGTH_LONG
                ).show()

                userGUI.emailEditText.text.clear()
                userGUI.passwordEditText.text.clear()
                userGUI.firstNameEditText.text.clear()
                userGUI.lastNameEditText.text.clear()
                userGUI.descriptionEditText.text.clear()
                userGUI.countryEditText.text.clear()
                userGUI.cookingExperienceEditText.text.clear()
                userGUI.cookingPreferenceEditText.text.clear()

                val intent = Intent(
                    this@RegistrationActivity,
                    AuthorisedActivity::class.java
                )
                intent.putExtra("user_id", userData.userId)
                startActivity(intent)
            }.addOnFailureListener { err ->
                Toast.makeText(
                    this@RegistrationActivity,
                    "Error ${err.message}", Toast.LENGTH_LONG
                ).show()
            }
            .addOnCanceledListener {
                Toast.makeText(
                    this@RegistrationActivity,
                    "Data cancelled", Toast.LENGTH_LONG
                ).show()
            }
    }
private fun saveRecipe() {

    dbRecipeRef = FirebaseDatabase.getInstance().getReference("Recipes")
    val recipe = Recipe(
        recipeId = "30",
        name = "Простой десерт с нутеллой и бананом",
        cuisine = "Домашняя кухня",
        ingredients = "•\tТостовый хлеб.\n" +
                "•\tНутелла.\n" +
                "•\tБанан – 1 шт.\n",
        recipe = "На тостовый хлеб намазать нутеллу, а сверху положить нарезанный банан.\n" +
                "Приятного аппетита!\n",
        cookingTime = 5
    )
    dbRecipeRef.child(recipe.recipeId!!).setValue(recipe)
}
}