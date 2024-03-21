package com.example.recipecasket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.recipecasket.R
import com.example.recipecasket.model.UserAdditionalData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnLogin: Button

    private lateinit var auth: FirebaseAuth

    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.etProfileName)
        passwordEditText = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLoginDetails)

        dbRef = FirebaseDatabase.getInstance().getReference("Users")
        auth = FirebaseAuth.getInstance()

        btnLogin.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (email.isEmpty()) {
            emailEditText.error = "Please enter email"
            return
        }
        if (password.isEmpty()) {
            passwordEditText.error = "Please enter password"
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    emailEditText.text.clear()
                    passwordEditText.text.clear()

                    Toast.makeText(
                        this@LoginActivity,
                        "Login successful", Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(
                        this@LoginActivity,
                        AuthorisedActivity::class.java
                    )
                    intent.putExtra("user_id", user.uid)
                    startActivity(intent)
                }
            } else {
                // Login failed
                val exception = task.exception
                // Handle the login failure
                if (exception is FirebaseAuthException) {
                    val errorCode = exception.errorCode

                    // Handle specific error codes or display the error message
                    when (errorCode) {
                        "ERROR_INVALID_CREDENTIAL" -> {
                            emailEditText.error = "Invalid credential"
                        }
                        "ERROR_INVALID_EMAIL" -> {
                            emailEditText.error = "Email is invalid"
                        }

                        "ERROR_WRONG_PASSWORD" -> {
                            passwordEditText.error = "Password is wrong"
                        }

                        "ERROR_USER_DISABLED" -> {
                            emailEditText.error = "Password is disabled on server"
                        }
                        else -> {
                            emailEditText.error = "Try another email"
                            passwordEditText.error = "Try another password"
                        }
                    }
                } else {
                    emailEditText.error = "Try another email"
                    passwordEditText.error = "Try another password"
                }
            }
        }
    }
}