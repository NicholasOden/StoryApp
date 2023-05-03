package com.example.picodiploma.storyapp

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    private lateinit var imageCapture: ImageCapture
    private lateinit var outputDirectory: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        // Set up the camera preview
        val previewView = findViewById<PreviewView>(R.id.viewFinder)
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            // Camera provider is now ready to be used
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            try {
                // Unbind any existing use cases before attaching new ones
                cameraProvider.unbindAll()
                // Attach the camera preview and image capture use cases
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture)
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))

        // Set up the output directory for saving the captured image
        outputDirectory = getOutputDirectory()

        // Set up the capture button click listener
        val captureImage = findViewById<ImageView>(R.id.captureImage)
        captureImage.setOnClickListener {
            val imageCapture = imageCapture ?: return@setOnClickListener
            val photoFile = createFile(outputDirectory, FILENAME, PHOTO_EXTENSION)
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Display the captured image in the ImageView
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        val imageViewPreview = findViewById<ImageView>(R.id.imageViewPreview)
                        imageViewPreview.setImageBitmap(bitmap)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Log.e(TAG, "Error capturing image", exception)
                    }
                })
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun createFile(baseFolder: File, format: String, extension: String): File {
        return File(baseFolder, SimpleDateFormat(format, Locale.US)
            .format(System.currentTimeMillis()) + extension)
    }

    companion object {
        private const val TAG = "CameraActivity"
        private const val FILENAME = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val PHOTO_EXTENSION = ".jpg"
    }
}
