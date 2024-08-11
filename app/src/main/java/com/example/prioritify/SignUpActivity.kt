package com.example.prioritify

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prioritify.api.RegisterRequest
import com.example.prioritify.api.RegisterResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
                registerUser(name, email, password)
            }
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

        alreadyHaveAccountTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        Log.d("DATA", "$email $name $password")
        val request = RegisterRequest(name, email, password)
        Log.d("RegisterRequest", request.toString())

        val call = RetrofitClient.instance.registerUser(request)

        call.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val user = response.body()?.data?.user
                    val token = response.body()?.data?.token
                    Toast.makeText(this@SignUpActivity, "Registration successful: ${user?.full_name}", Toast.LENGTH_SHORT).show()

                    // Save the token or navigate to the next screen
                    val intent = Intent(this@SignUpActivity, LandingActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d("RESPONDE BODY", response.toString())
                    Toast.makeText(this@SignUpActivity, "Registration failed: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.d("Message", t.toString())
                Toast.makeText(this@SignUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
