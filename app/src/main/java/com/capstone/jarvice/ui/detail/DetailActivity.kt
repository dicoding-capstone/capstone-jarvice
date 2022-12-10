package com.capstone.jarvice.ui.detail

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.capstone.jarvice.databinding.ActivityDetailBinding
import com.capstone.jarvice.db.BookmarkJobList
import com.capstone.jarvice.model.UserPreference
import com.capstone.jarvice.network.ListJobsItem
import com.capstone.jarvice.ui.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

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

        val detailViewModel by viewModels<DetailViewModel> {
            ViewModelFactory(UserPreference.getInstance(dataStore),
                this@DetailActivity.application,
                detailJobs.company.toString())
        }

        detailViewModel.bookmarked.observe(this) {
            binding.toggleBookmark.isChecked = it
        }

        binding.toggleBookmark.setOnClickListener {
            val dataInsert = detailJobs.company?.let { it1 ->
                detailJobs.name?.let { it2 ->
                    detailJobs.image?.let { it3 ->
                        BookmarkJobList(it1,
                            it2, it3)
                    }
                }
            }

            if (detailViewModel.checkBookmark()!!) {
                detailViewModel.delete(dataInsert!!)
                binding.toggleBookmark.isChecked = false
            } else {
                detailViewModel.insert(dataInsert!!)
                binding.toggleBookmark.isChecked = true
            }

        }

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
        const val DETAIL_JOB = "detail_job"
    }
}