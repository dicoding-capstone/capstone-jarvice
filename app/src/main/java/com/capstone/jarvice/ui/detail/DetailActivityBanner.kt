package com.capstone.jarvice.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.ActivityOptionsCompat
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.ActivityDetailBannerBinding
import com.capstone.jarvice.network.JobsItem
import com.capstone.jarvice.network.ListJobsItem
import com.capstone.jarvice.ui.main.MainActivity

class DetailActivityBanner : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setDetailJobs()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setDetailJobs() {
        val detailJobs = intent.getParcelableExtra<JobsItem>(DETAIL_BANNER) as JobsItem
        binding.apply {
            tvJobFill.text = detailJobs.name
            tvCompanyFill.text = detailJobs.company
            tvCategoryFill.text = detailJobs.category
            tvLocationFill.text = detailJobs.location
            tvLevelFill.text = detailJobs.experience
            tvTimeFill.text = detailJobs.fulltime
            tvTagFill.text = detailJobs.salary.toString()

            btWebPage.setOnClickListener {
                detailJobs.web?.let { it1 -> goLink(it1) }
            }
        }
    }

    private fun goLink(link: String) {
        val uri = Uri.parse(link)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    companion object {
        const val DETAIL_BANNER = "detail_banner"
    }
}