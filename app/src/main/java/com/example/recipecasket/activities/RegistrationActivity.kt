package com.example.recipecasket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipecasket.R
import com.example.recipecasket.model.Recipe
import com.example.recipecasket.model.User
import com.example.recipecasket.model.UserGUI
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
    private lateinit var dbRecipeRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        userGUI = UserGUI(findViewById(android.R.id.content))
        btnRegistration = findViewById(R.id.btnRegistration)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        btnRegistration.setOnClickListener {
            saveUserData()
        }

            //saveRecipe()
    }

    private fun saveRecipe(){
        dbRecipeRef = FirebaseDatabase.getInstance().getReference("Recipes")
        val recipe = Recipe(
            recipeId = "10",
            name = "Галета с томатами",
            cuisine = "Итальянская кухня",
            ingredients = "Тесто:\n" +
                    "\tМука – 270 гр. \n" +
                    "\tЯйцо – 1 шт.\n" +
                    "\tОливковое масло – 60 мл.\n" +
                    "\tВода – 60 мл. \n" +
                    "\tСоль. \n" +
                    "Начинка:\n" +
                    "\tТоматы – 3 шт.\n" +
                    "\tМоцарелла – 3 шт.\n" +
                    "\tИтальянские травы.\n" +
                    "\tОливковое масло.\n" +
                    "\tЧёрный перец.\n" +
                    "\tСоль. \n" +
                    "Кунжут для украшения.\n" +
                    "Желток.\n",
            recipe = "1. Просеять муку с солью в большую миску. В другой миске смешать теплую воду, оливковое масло и яйцо, перелить в миску с мукой. Замесить мягкое эластичное тесто, скатать его в шар и положить в миску. Накрыть пищевой пленкой и оставить на 1 ч.\n" +
                    "2. Разогреть духовку до 180 °С. Раскатать тесто в круг диаметром 36 см. Застелить противень бумагой для выпечки и выложить тесто.\n" +
                    "3. Нарезать помидоры, переложить их в миску, полить оливковым маслом, посыпать перцем, солью, итальянскими травами, перемешать.\n" +
                    "4. Выложить на тесто начинку, отступив от края 4 см. Завернуть края теста на начинку. Смажьте края желтком. Поставить галету в духовку и выпекайте 40–45 мин., пока тесто не станет золотистым. \n" +
                    "5. Остудите на противне, подавайте галету теплой, украсив её моцареллой и кунжутом. \n",
            cookingTime = 150
        )
        dbRecipeRef.child(recipe.recipeId!!).setValue(recipe)
    }

    private fun saveUserData() {
        val profileName = userGUI.profileNameEditText.text.toString()
        val password = userGUI.passwordEditText.text.toString()
        val firstName = userGUI.firstNameEditText.text.toString()
        val lastName = userGUI.lastNameEditText.text.toString()
        val description = userGUI.descriptionEditText.text.toString()
        val country = userGUI.countryEditText.text.toString()
        val cookingExperience = userGUI.cookingExperienceEditText.text.toString().toIntOrNull()
        val cookingPreference = userGUI.cookingPreferenceEditText.text.toString()

        if (profileName.isEmpty()) {
            userGUI.profileNameEditText.error = "Please enter profile name"
            return
        }
        if (password.isEmpty()) {
            userGUI.passwordEditText.error = "Please enter password"
            return
        }

        // Check for uniqueness of profile name
        dbRef.orderByChild("profileName").equalTo(profileName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // A user with the same profile name already exists
                        Toast.makeText(
                            this@RegistrationActivity,
                            "Profile name already exists", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Profile name is unique, proceed with registration
                        val userId = dbRef.push().key!!

                        val user = User(
                            userId = userId,
                            profileName = profileName,
                            password = password,
                            firstName = firstName,
                            lastName = lastName,
                            description = description,
                            registrationDate = SimpleDateFormat(
                                "yyyy-MM-dd", Locale.getDefault()
                            ).format(Date()),
                            country = country,
                            cookingExperience = cookingExperience,
                            cookingPreference = cookingPreference
                        )

                        dbRef.child(userId).setValue(user)
                            .addOnCompleteListener {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Register successful", Toast.LENGTH_LONG
                                ).show()

                                // Clear input fields
                                userGUI.profileNameEditText.text.clear()
                                userGUI.passwordEditText.text.clear()
                                userGUI.firstNameEditText.text.clear()
                                userGUI.lastNameEditText.text.clear()
                                userGUI.descriptionEditText.text.clear()
                                userGUI.countryEditText.text.clear()
                                userGUI.cookingExperienceEditText.text.clear()
                                userGUI.cookingPreferenceEditText.text.clear()

                                val intent = Intent(this@RegistrationActivity,
                                    AuthorisedActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { err ->
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
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@RegistrationActivity,
                        "Error: ${databaseError.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}