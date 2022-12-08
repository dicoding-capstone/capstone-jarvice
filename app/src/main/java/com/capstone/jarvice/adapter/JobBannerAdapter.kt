package com.capstone.jarvice.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.jarvice.databinding.ItemRowBannerJobBinding
import com.capstone.jarvice.network.JobsItem

class JobBannerAdapter(private val listJobBanner: ArrayList<JobsItem>) :
    RecyclerView.Adapter<JobBannerAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowBannerJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (img) = listJobBanner[position]
        Glide.with(holder.itemView.context)
            .load(img)
            .into(holder.binding.imgItemPhoto)
    }

    override fun getItemCount(): Int = listJobBanner.size

    class ListViewHolder(var binding: ItemRowBannerJobBinding) :
        RecyclerView.ViewHolder(binding.root)

}
