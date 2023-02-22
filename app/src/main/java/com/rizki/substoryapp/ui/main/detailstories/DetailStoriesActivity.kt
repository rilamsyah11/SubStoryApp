package com.rizki.substoryapp.ui.main.detailstories

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.rizki.substoryapp.R
import com.rizki.substoryapp.databinding.ActivityDetailStoriesBinding
import com.rizki.substoryapp.model.Stories

class DetailStoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.details_story)

        val stories = intent.getParcelableExtra<Stories>(DETAIL_STORIES) as Stories
        Glide.with(this)
            .load(stories.Photo)
            .into(binding.imgDetailPhoto)
        binding.tvDetailName.text = stories.Name
        binding.tvDetailDescription.text = stories.Description
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val DETAIL_STORIES = "detail_stories"
    }
}