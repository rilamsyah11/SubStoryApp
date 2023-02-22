package com.rizki.substoryapp.ui.main.stories


import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rizki.substoryapp.databinding.ItemStoriesBinding
import com.rizki.substoryapp.model.Stories
import com.rizki.substoryapp.response.ListStory
import com.rizki.substoryapp.ui.main.detailstories.DetailStoriesActivity

class StoriesAdapter: PagingDataAdapter<ListStory, StoriesAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoriesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if(data != null) {
            holder.bind(data)
        }
    }

    class StoryViewHolder(private val binding: ItemStoriesBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(dataStory: ListStory) {
            Glide.with(binding.root.context)
                .load(dataStory.PhotoUrl)
                .into(binding.imagesPhoto)
            binding.tvName.text = dataStory.Name

            binding.root.setOnClickListener {
                val stories = Stories(
                    dataStory.Name,
                    dataStory.PhotoUrl,
                    dataStory.Description,
                    null,
                    null
                )

                val intent = Intent(binding.root.context, DetailStoriesActivity::class.java)
                intent.putExtra(DetailStoriesActivity.DETAIL_STORIES, stories)
                binding.root.context.startActivity(intent)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItems: ListStory, newItems: ListStory): Boolean {
                return oldItems == newItems
            }

            override fun areContentsTheSame(
                oldItems: ListStory,
                newItems: ListStory
            ): Boolean {
                return oldItems == newItems
            }

        }
    }
}