package com.rizki.substoryapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rizki.substoryapp.databinding.LoadingItemBinding

class LoadingAdapter(private val retry: () -> Unit) : LoadStateAdapter<LoadingAdapter.LoadingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadingViewHolder {
        val binding = LoadingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadingViewHolder(binding, retry)
    }
    override fun onBindViewHolder(holder: LoadingViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }
    class LoadingViewHolder(private val binding: LoadingItemBinding, retry: () -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.retryBtn.setOnClickListener { retry.invoke() }
        }
        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMessage.text = loadState.error.localizedMessage
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryBtn.isVisible = loadState is LoadState.Error
            binding.errorMessage.isVisible = loadState is LoadState.Error
        }
    }
}