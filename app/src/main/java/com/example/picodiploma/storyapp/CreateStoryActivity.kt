package com.example.picodiploma.storyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.service.controls.ControlsProviderService.TAG
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.databinding.ActivityCreateStoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.File
import java.io.IOException

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding

    private var imageUri: Uri? = null
    private var token: String? = null


    private val permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    private val PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences("storyapp", MODE_PRIVATE)
        token = sharedPrefs.getString("token", null)


        binding.imageViewPreview.setOnClickListener {
            if (checkPermission(permissions)) {
                openCamera()
            } else {
                requestPermission(permissions, PERMISSION_CODE)
            }
        }

        binding.btnPost.setOnClickListener {
            val description = binding.editTextPostStory.text.toString()
            if (description.isEmpty()) {
                Toast.makeText(this, "Please fill in the description", Toast.LENGTH_SHORT).show()
            } else {
                val imageFile = File(imageUri?.path)
                val apiServiceHelper = ApiServiceHelper(token)

                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val compressedImageFile = imageFile?.reduceFileImage(1000000) // maximum file size of 1MB
                        if (compressedImageFile != null) {
                            val response = apiServiceHelper.uploadStory(description, compressedImageFile, null, null)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CreateStoryActivity, "Story uploaded successfully", Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(this@CreateStoryActivity, "Failed to compress image file", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        val errorMessage = e.message ?: "Unknown error"
                        val errorBody = (e as? HttpException)?.response()?.errorBody()?.string()
                        if (errorBody != null) {
                            Log.e("ApiServiceHelper", "Failed to upload story: $errorBody")
                        }
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CreateStoryActivity, "Failed to upload story: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: IOException) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@CreateStoryActivity, "Failed to read image file", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun openCamera() {
        // Create a temporary file to store the captured image
        imageUri = createImageFile(this)

        if (imageUri != null) {
            // Launch the camera app and pass the URI of the temporary file as an extra
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
        } else {
            Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkPermission(permissionArray: Array<String>): Boolean {
        return permissionArray.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(permissionArray: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissionArray, requestCode)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                // Load the captured image from the URI
                try {
                    // Load and resize the image using Glide
                    val glide = Glide.with(this)
                    val requestBuilder = glide.asBitmap().load(imageUri).apply(RequestOptions().override(1024, 1024))
                    requestBuilder.apply(RequestOptions().centerCrop())
                    requestBuilder.into(binding.imageViewPreview)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
