package com.example.picodiploma.storyapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.api.Response.UserRegistration
import kotlinx.coroutines.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnRegister: Button

    private lateinit var apiServiceHelper: ApiServiceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextName = findViewById(R.id.editTextNameSignUp)
        editTextEmail = findViewById(R.id.editTextEmailLogin)
        editTextPassword = findViewById(R.id.editTextPasswordLogin)
        btnRegister = findViewById(R.id.btnRegister)

        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        apiServiceHelper = ApiServiceHelper(token)

        btnRegister.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            val userRegistration = UserRegistration(name, email, password)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val registerResponse = apiServiceHelper.registerUser(userRegistration)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Registration successful: ${registerResponse.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@SignUpActivity,
                            "Registration failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
