package com.example.picodiploma.storyapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.picodiploma.storyapp.databinding.ActivityCreateStoryBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CreateStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateStoryBinding

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
                // Call your API to upload the story
            }
        }
    }

    private fun openCamera() {
        // Create a temporary file to store the captured image
        val imageUri = createImageFile(this)

        // Launch the camera app and pass the URI of the temporary file as an extra
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
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
        for (permission in permissionArray) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermission(permissionArray: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(this, permissionArray, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                val imageUri = data?.data
                if (imageUri != null) {
                    try {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                        binding.imageViewPreview.setImageBitmap(imageBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

}
