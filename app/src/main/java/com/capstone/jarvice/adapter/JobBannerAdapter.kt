package com.capstone.jarvice.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.jarvice.databinding.ItemRowBannerJobBinding
import com.capstone.jarvice.network.JobsItem
import com.capstone.jarvice.ui.detail.DetailActivity
import com.capstone.jarvice.ui.detail.DetailActivityBanner

class JobBannerAdapter(private val listJobBanner: ArrayList<JobsItem>) :
    RecyclerView.Adapter<JobBannerAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemRowBannerJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listJobBanner[position])
    }

    override fun getItemCount(): Int = listJobBanner.size

    class ListViewHolder(private var binding: ItemRowBannerJobBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(job: JobsItem) {
            Glide.with(itemView.context)
                .load(job.promopic)
                .into(binding.imgItemPhoto)

            binding.cardView.setOnClickListener {
                val moveDetailJobs =
                    Intent(itemView.context, DetailActivityBanner::class.java)
                moveDetailJobs.putExtra(DetailActivityBanner.DETAIL_BANNER, job)

                itemView.context.startActivity(moveDetailJobs,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity)
                        .toBundle())
            }

        }

    }
}