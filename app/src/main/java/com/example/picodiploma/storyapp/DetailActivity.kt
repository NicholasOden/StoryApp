package com.example.picodiploma.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.MenuItem
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.picodiploma.storyapp.api.ApiServiceHelper
import com.example.picodiploma.storyapp.api.Response.StoryDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var apiServiceHelper: ApiServiceHelper
    private var storyId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        // Enable content transitions
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.sharedElementEnterTransition = TransitionInflater.from(this)
            .inflateTransition(R.transition.shared_element_transition)
        window.sharedElementExitTransition = TransitionInflater.from(this)
            .inflateTransition(R.transition.shared_element_transition)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        apiServiceHelper = ApiServiceHelper(getToken())

        storyId = intent.getStringExtra("STORY_ID") ?: ""
        getStoryDetail(storyId)
    }

    private fun getToken(): String? {
        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        return sharedPreferences.getString("token", "")
    }

    private fun getStoryDetail(id: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val response = apiServiceHelper.getStoryDetail(id)
            withContext(Dispatchers.Main) {
                if (response.error) {
                    Toast.makeText(this@DetailActivity, response.message, Toast.LENGTH_SHORT).show()
                } else {
                    displayStoryDetail(response.data)
                }
            }
        }
    }

    private fun displayStoryDetail(storyDetail: StoryDetailResponse?) {
        Log.d("StoryDetail", "Name: ${storyDetail?.name}")
        Log.d("StoryDetail", "Description: ${storyDetail?.description}")
        Log.d("StoryDetail", "Created: ${storyDetail?.createdAt}")

        findViewById<TextView>(R.id.textViewDetailName).text = storyDetail?.name ?: ""
        findViewById<TextView>(R.id.textViewDetailDescription).text = storyDetail?.description ?: ""
        findViewById<TextView>(R.id.textViewDetailCreated).text = storyDetail?.getCreatedDate()?.let { formatDate(it) } ?: ""
        Glide.with(this)
            .load(storyDetail?.imageUrl)
            .placeholder(R.drawable.logo)
            .error(R.drawable.logo)
            .into(findViewById<ImageView>(R.id.imageViewDetail))
    }

    private fun formatDate(date: Date?): String? {
        if (date == null) return null
        val format = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US)
        return format.format(date)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
