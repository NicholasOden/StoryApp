package com.example.picodiploma.storyapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picodiploma.storyapp.Adapter.StoryAdapter
import com.example.picodiploma.storyapp.Model.ApiServiceHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StoryAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var apiServiceHelper: ApiServiceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.rVMain)
        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = StoryAdapter(listOf())
        recyclerView.adapter = adapter

        val sharedPreferences = getSharedPreferences("storyapp", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")

        apiServiceHelper = ApiServiceHelper(token)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storyList = apiServiceHelper.getStoryList()
                Log.d("MainActivity", "Story list: $storyList")
                withContext(Dispatchers.Main) {
                    adapter.setData(storyList)
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error fetching stories: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch stories: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}


