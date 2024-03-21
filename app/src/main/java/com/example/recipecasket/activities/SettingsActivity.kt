package com.example.recipecasket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipecasket.R
import com.example.recipecasket.model.UserAdditionalData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Locale

class SettingsActivity : AppCompatActivity() {

    private lateinit var tvDate: TextView
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etProfileDescription: EditText
    private lateinit var etCountry: EditText
    private lateinit var etCookingExperience: EditText
    private lateinit var etCookingPreference: EditText

    private lateinit var btnUpdate: Button
    private lateinit var btnSignOut: Button
    private lateinit var btnDeleteAccount: Button
    private lateinit var userData: UserAdditionalData
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        tvDate = findViewById(R.id.tvRegistrationDate)
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etProfileDescription = findViewById(R.id.etProfileDescription)
        etCountry = findViewById(R.id.etCountry)
        etCookingExperience = findViewById(R.id.etCookingExperience)
        etCookingPreference = findViewById(R.id.etCookingPreference)

        btnUpdate = findViewById(R.id.btnUpdate)
        btnUpdate.isEnabled = false
        btnSignOut = findViewById(R.id.btnSignOut)
        btnDeleteAccount = findViewById(R.id.btnDelete)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")

        dbRef.child(intent.getStringExtra("user_id").toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Handle the retrieved data here
                    if (dataSnapshot.exists()) {
                        userData = dataSnapshot.getValue(UserAdditionalData::class.java)!!
                        btnUpdate.isEnabled = true
                        processDataFetching(userData)
                    } else {
                        // Handle the case when the key does not exist in the database
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle any errors that occur during the data retrieval
                }
            })

        btnUpdate.setOnClickListener {
            updateUserData(userData)
        }

        btnSignOut.setOnClickListener {
            startMainActivity()
        }

        btnDeleteAccount.setOnClickListener {
            deleteAccount()
        }
    }

    private fun processDataFetching(userData: UserAdditionalData) {
        tvDate.text = formatDate(userData.registrationDate!!)
        etFirstName.setText(userData.firstName)
        etLastName.setText(userData.lastName)
        etProfileDescription.setText(userData.description)
        etCountry.setText(userData.country)
        etCookingExperience.setText(userData.cookingExperience.toString())
        etCookingPreference.setText(userData.cookingPreference)
    }

    private fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)

        val date = inputFormat.parse(inputDate)
        val outputDate = outputFormat.format(date!!)
        return "Registration date: $outputDate"
    }

    private fun startMainActivity() {
        val intent = Intent(
            this@SettingsActivity,
            MainActivity::class.java
        )
        startActivity(intent)
    }

    private fun deleteAccount() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.delete()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Deleted account successful", Toast.LENGTH_LONG
                    ).show()
                    dbRef.child(intent.getStringExtra("user_id").toString()).removeValue()
                        .addOnSuccessListener{
                            startMainActivity()
                        }.addOnFailureListener{
                            Toast.makeText(
                                this,
                                "Failed to delete additional user data",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    Toast.makeText(
                        this,
                        "Failed to delete account: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun updateUserData(userData: UserAdditionalData) {
        var isCorrectData = true
        try {
            userData.cookingExperience = etCookingExperience.text.toString().toInt()
        } catch (e: NumberFormatException) {
            etCookingExperience.error = "Only digits are allowed here!"
            isCorrectData = false
        }
        if (isCorrectData) {
            userData.firstName = etFirstName.text.toString()
            userData.lastName = etLastName.text.toString()
            userData.description = etProfileDescription.text.toString()
            userData.country = etCountry.text.toString()
            userData.cookingPreference = etCookingPreference.text.toString()

            dbRef.child(userData.userId!!).setValue(userData)
                .addOnCompleteListener {
                    Toast.makeText(
                        this,
                        "Modify successful", Toast.LENGTH_LONG
                    ).show()
                }
        }
        val intent = Intent(
            this@SettingsActivity,
            AuthorisedActivity::class.java
        )
        intent.putExtra("user_id", userData.userId)
        startActivity(intent)
    }
}