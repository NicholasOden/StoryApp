package com.example.picodiploma.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.picodiploma.storyapp.Model.ApiServiceHelper
import com.example.picodiploma.storyapp.Model.RegisterResponse
import com.example.picodiploma.storyapp.Model.UserRegistration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var editTextName: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnRegister: Button

    private val apiServiceHelper = ApiServiceHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        editTextName = findViewById(R.id.editTextNameSignUp)
        editTextEmail = findViewById(R.id.editTextEmailSignUp)
        editTextPassword = findViewById(R.id.editTextPasswordSignUp)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val name = editTextName.text.toString()
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()

            val userRegistration = UserRegistration(name, email, password)

            apiServiceHelper.registerUser(userRegistration, object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful) {
                        val registerResponse = response.body()
                        Toast.makeText(this@SignUpActivity, "Registration successful: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorResponse = response.errorBody()?.string()
                        Toast.makeText(this@SignUpActivity, "Registration failed: $errorResponse", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@SignUpActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
