package com.capstone.jarvice.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.jarvice.databinding.ActivityDetailBinding
import com.capstone.jarvice.network.ListJobsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
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
        val detailJobs = intent.getParcelableExtra<ListJobsItem>(DETAIL_JOB) as ListJobsItem

//        var isChecked = false
//        CoroutineScope(Dispatchers.IO).launch {
//            val count = detailViewModel.bookmarkCheck(detailJobs.name)
//            withContext(Dispatchers.Main) {
//                if (count != null) {
//                    binding.toggleBookmark.isChecked = true
//                    isChecked = true
//                } else {
//                    binding.toggleBookmark.isChecked = false
//                    isChecked = false
//                }
//            }
//        }

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

//            toggleBookmark.setOnClickListener {
//                isChecked = !isChecked
//                if (isChecked) {
//                    detailJobs.image?.let { it1 ->
//                        detailJobs.company?.let { it2 ->
//                            detailViewModel.addBookmark(it1, detailJobs.name,
//                                it2)
//                        }
//                    }
//                } else {
//                    detailViewModel.removeBookmark(detailJobs.name)
//                }
//                toggleBookmark.isChecked = isChecked
//            }
        }
    }


    private fun goLink(link: String) {
        val uri = Uri.parse(link)
        startActivity(Intent(Intent.ACTION_VIEW, uri))
    }

    companion object {
        const val DETAIL_JOB = "detail_job"
    }
}