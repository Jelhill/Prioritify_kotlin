package com.example.prioritify

import SessionManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prioritify.api.LoginRequest
import com.example.prioritify.api.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter your email and password", Toast.LENGTH_SHORT).show()
            } else {
                loginUser(email, password)
            }
        }

        forgotPasswordTextView.setOnClickListener {
            Toast.makeText(this, "Forgot Password clicked", Toast.LENGTH_SHORT).show()
        }

        appleSignInButton.setOnClickListener {
            Toast.makeText(this, "Apple Sign In clicked", Toast.LENGTH_SHORT).show()
        }

        googleSignInButton.setOnClickListener {
            Toast.makeText(this, "Google Sign In clicked", Toast.LENGTH_SHORT).show()
        }

        facebookSignInButton.setOnClickListener {
            Toast.makeText(this, "Facebook Sign In clicked", Toast.LENGTH_SHORT).show()
        }

        signUpTextView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val sessionManager = SessionManager(this)
        val retrofitClient = RetrofitClient.getInstance(sessionManager)

        val request = LoginRequest(email, password)
        val call = retrofitClient.loginUser(request)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.d("RESPONSE RECIVED", response.body().toString());
                    val user = response.body()?.data?.user
                    val token = response.body()?.data?.token
                    Log.d("USER", user.toString());
                    Log.d("TOKEN", token.toString());
                    Toast.makeText(this@MainActivity, "Welcome, ${user?.full_name}", Toast.LENGTH_SHORT).show()

                    // Save the token using SessionManager
                    sessionManager.saveAuthToken(token!!)

                    // Navigate to the next screen
                    val intent = Intent(this@MainActivity, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Login failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
