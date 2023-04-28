package com.example.picodiploma.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.picodiploma.storyapp.Model.ApiServiceHelper
import com.example.picodiploma.storyapp.Model.RegisterResponse
import com.example.picodiploma.storyapp.Model.UserRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
        editTextEmail = findViewById(R.id.editTextEmailSignUp)
        editTextPassword = findViewById(R.id.editTextPasswordSignUp)
        btnRegister = findViewById(R.id.btnRegister)

        apiServiceHelper = ApiServiceHelper()

        btnRegister.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            val userRegistration = UserRegistration(name, email, password)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val registerResponse = apiServiceHelper.registerUser(userRegistration)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignUpActivity, "Registration successful: ${registerResponse.message}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@SignUpActivity, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

