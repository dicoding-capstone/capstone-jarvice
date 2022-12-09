package com.capstone.jarvice.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.jarvice.databinding.ItemColJobBinding
import com.capstone.jarvice.network.ListJobsItem
import com.capstone.jarvice.ui.detail.DetailActivity

class ListJobAdapter(private val listJob: ArrayList<ListJobsItem>) :
    RecyclerView.Adapter<ListJobAdapter.ListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemColJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listJob[position])
    }

    override fun getItemCount(): Int = listJob.size

    class ListViewHolder(private var binding: ItemColJobBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(job: ListJobsItem) {
            Glide.with(itemView.context)
                .load(job.image)
                .into(binding.companyLogo)
            binding.apply {
                tvJobName.text = job.name
                tvCompanyName.text = job.company
                cardListJobs.setOnClickListener {
                    val moveDetailJobs =
                        Intent(itemView.context, DetailActivity::class.java)
                    moveDetailJobs.putExtra(DetailActivity.DETAIL_JOB, job)

                    itemView.context.startActivity(moveDetailJobs,
                        ActivityOptionsCompat.makeSceneTransitionAnimation(itemView.context as Activity)
                            .toBundle())
                }
            }
        }
    }
}