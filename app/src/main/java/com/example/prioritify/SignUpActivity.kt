package com.example.prioritify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val nameEditText: EditText = findViewById(R.id.etName)
        val emailEditText: EditText = findViewById(R.id.etEmail)
        val passwordEditText: EditText = findViewById(R.id.etPassword)
        val agreeTermsCheckBox: CheckBox = findViewById(R.id.cbAgreeTerms)
        val signUpButton: Button = findViewById(R.id.btnSignUp)
        val appleSignInButton: ImageButton = findViewById(R.id.buttonApple)
        val googleSignInButton: ImageButton = findViewById(R.id.buttonGoogle)
        val facebookSignInButton: ImageButton = findViewById(R.id.buttonFacebook)
        val alreadyHaveAccountTextView: TextView = findViewById(R.id.tvAlreadyHaveAccount)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val agreeTerms = agreeTermsCheckBox.isChecked

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !agreeTerms) {
                Toast.makeText(this, "Please fill in all fields and agree to the terms", Toast.LENGTH_SHORT).show()
            } else {
                // Handle sign up logic
                Toast.makeText(this, "Sign Up clicked", Toast.LENGTH_SHORT).show()
            }
        }

        appleSignInButton.setOnClickListener {
            // Handle Apple sign in logic
            Toast.makeText(this, "Apple Sign In clicked", Toast.LENGTH_SHORT).show()
        }

        googleSignInButton.setOnClickListener {
            // Handle Google sign in logic
            Toast.makeText(this, "Google Sign In clicked", Toast.LENGTH_SHORT).show()
        }

        facebookSignInButton.setOnClickListener {
            // Handle Facebook sign in logic
            Toast.makeText(this, "Facebook Sign In clicked", Toast.LENGTH_SHORT).show()
        }

        alreadyHaveAccountTextView.setOnClickListener {
            // Navigate back to Sign In page
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
