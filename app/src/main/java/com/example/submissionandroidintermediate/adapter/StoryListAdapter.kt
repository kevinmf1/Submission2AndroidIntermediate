package com.example.submissionandroidintermediate.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.databinding.StoryItemRowBinding
import com.example.submissionandroidintermediate.dataclass.StoryDetail
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class StoryListAdapter(private val listStory: List<StoryDetail>) :
    RecyclerView.Adapter<StoryListAdapter.ListViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryDetail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            StoryItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])
        }
    }

    class ListViewHolder(private var binding: StoryItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: StoryDetail) {
            binding.itemStory = data
            binding.executePendingBindings()
        }
    }

    override fun getItemCount(): Int = listStory.size

    companion object {

        @JvmStatic
        @BindingAdapter("setPhoto")
        fun setPhoto(img_photo: ImageView, url: String) {
            Glide.with(img_photo)
                .load(url)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .fallback(R.drawable.ic_launcher_foreground)
                .into(img_photo)
        }

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
