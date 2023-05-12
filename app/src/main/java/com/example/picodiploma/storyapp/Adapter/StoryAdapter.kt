package com.example.picodiploma.storyapp.Adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picodiploma.storyapp.DetailActivity
import com.example.picodiploma.storyapp.api.Response.Story
import com.example.picodiploma.storyapp.R

class StoryAdapter(private var storyList: List<Story>) :
    RecyclerView.Adapter<StoryAdapter.ViewHolder>() {

    fun setData(newStoryList: List<Story>) {
        storyList = newStoryList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewMain: ImageView = itemView.findViewById(R.id.imageViewMain)
        val textViewTitleMain: TextView = itemView.findViewById(R.id.textViewTitleMain)
        val textViewDescriptionMain: TextView = itemView.findViewById(R.id.textViewDescriptionMain)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = storyList.getOrNull(position)
        if (story != null) {
            holder.textViewTitleMain.text = story.name ?: ""
            holder.textViewDescriptionMain.text = story.description ?: ""
            if (!story.photoUrl.isNullOrEmpty()) {
                Glide.with(holder.imageViewMain.context)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.logo)
                    .into(holder.imageViewMain)
            } else {
                holder.imageViewMain.setImageResource(R.drawable.logo)
            }
            holder.cardView.setOnClickListener {
                val context = holder.cardView.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("STORY_ID", story?.id)

                // Shared element transition setup
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    context as Activity,
                    holder.imageViewMain,
                    "storyImage"
                )
                ActivityCompat.startActivity(context, intent, options.toBundle())
            }
        }
    }

    override fun getItemCount(): Int {
        return storyList.size
    }
}
