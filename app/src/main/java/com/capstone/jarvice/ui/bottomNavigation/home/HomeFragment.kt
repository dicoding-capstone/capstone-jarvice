package com.capstone.jarvice.ui.bottomNavigation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.jarvice.adapter.JobBannerAdapter
import com.capstone.jarvice.databinding.FragmentHomeBinding
import com.capstone.jarvice.network.JobsItem
import com.capstone.jarvice.utils.ShowLoading

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var showLoading: ShowLoading
    private lateinit var progressBar: View
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(
            inflater,
            container,
            false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLoading = ShowLoading()
        progressBar = binding.progressBar

        showViewModel()

    }

    private fun showViewModel() {
        homeViewModel.getBannerJob()
        homeViewModel.isLoading.observe(requireActivity()) {
            showLoading.showLoading(it, progressBar)
        }

        homeViewModel.job.observe(requireActivity()) {
            setBannerJob(it)
        }

        homeViewModel.toast.observe(requireActivity()) {
            toast(it)
        }
    }

    private fun setBannerJob(job: List<JobsItem>) {
        val listJobs = ArrayList<JobsItem>()
        for (i in job) {
            val jobBanner = JobsItem(i.promopic)
            listJobs.addAll(listOf(jobBanner))
        }

        val adapterBanner = JobBannerAdapter(listJobs)
        binding.rvBannerJob.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterBanner
            }
    }

    private fun toast(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}