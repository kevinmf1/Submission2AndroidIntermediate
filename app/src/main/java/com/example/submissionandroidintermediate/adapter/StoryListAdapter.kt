package com.example.submissionandroidintermediate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.database.ListStoryDetail
import com.example.submissionandroidintermediate.databinding.StoryItemRowBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StoryListAdapter :
    PagingDataAdapter<ListStoryDetail, StoryListAdapter.ListViewHolder>(StoryDetailDiffCallback()) {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStoryDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            StoryItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        holder.itemView.setOnClickListener {
            getItem(position)?.let { it1 -> onItemClickCallback.onItemClicked(it1) }
        }
    }

    class ListViewHolder(private var binding: StoryItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryDetail) {
            binding.authorStory.text = data.name
            binding.dateStory.text = formatDateToString(data.createdAt.toString())
            Glide.with(itemView.context)
                .load(data.photoUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(binding.imageStory)
        }
    }

    class StoryDetailDiffCallback : DiffUtil.ItemCallback<ListStoryDetail>() {
        override fun areItemsTheSame(oldItem: ListStoryDetail, newItem: ListStoryDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ListStoryDetail,
            newItem: ListStoryDetail
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {

        @JvmStatic
        fun formatDateToString(dateString: String): String {
            val inputDateFormat =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val date: Date?
            var outputDate = ""

            try {
                date = inputDateFormat.parse(dateString)
                outputDate = outputDateFormat.format(date!!)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return outputDate
        }
    }
}