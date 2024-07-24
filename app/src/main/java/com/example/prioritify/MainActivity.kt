package com.example.prioritify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEditText: EditText = findViewById(R.id.editTextEmail)
        val passwordEditText: EditText = findViewById(R.id.editTextPassword)
        val signInButton: Button = findViewById(R.id.buttonSignIn)
        val forgotPasswordTextView: TextView = findViewById(R.id.tvForgotPassword)
        val appleSignInButton: ImageButton = findViewById(R.id.buttonApple)
        val googleSignInButton: ImageButton = findViewById(R.id.buttonGoogle)
        val facebookSignInButton: ImageButton = findViewById(R.id.buttonFacebook)
        val signUpTextView: TextView = findViewById(R.id.tvSignUp)

        signInButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Handle sign in logic
            Toast.makeText(this, "Sign In clicked", Toast.LENGTH_SHORT).show()

            // After successful login, navigate to LandingActivity
            val intent = Intent(this, LandingActivity::class.java)
            startActivity(intent)
            finish()
        }

        forgotPasswordTextView.setOnClickListener {
            // Handle forgot password logic
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
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

//        signUpTextView.setOnClickListener {
//            // Handle sign up logic
//            Toast.makeText(this, "Sign Up clicked", Toast.LENGTH_SHORT).show()
//        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

    }
}
