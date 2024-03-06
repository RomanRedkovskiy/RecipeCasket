package com.example.recipecasket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipecasket.R
import com.example.recipecasket.model.User
import com.example.recipecasket.model.UserGUI
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {

    private lateinit var profileNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnLogin: Button

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        profileNameEditText = findViewById(R.id.etProfileName)
        passwordEditText = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLoginDetails)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")


        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val profileName = profileNameEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (profileName.isEmpty()) {
            profileNameEditText.error = "Please enter profile name"
            return
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Please enter password"
            return
        }

        dbRef.orderByChild("profileName").equalTo(profileName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var user: User? = null
                    for (snapshot in dataSnapshot.children) {
                        val userData = snapshot.getValue(User::class.java)
                        if (userData != null && userData.password == password) {
                            user = userData
                            break
                        }
                    }

                    if (user != null) {
                        // User with matching login and password found
                        Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()
                        // Do something with the user data, e.g., navigate to the main activity
                         val intent = Intent(this@LoginActivity, AuthorisedActivity::class.java)
                         startActivity(intent)
                    } else {
                        // No user with matching login and password found
                        Toast.makeText(this@LoginActivity, "Invalid login or password", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@LoginActivity, "Error: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}