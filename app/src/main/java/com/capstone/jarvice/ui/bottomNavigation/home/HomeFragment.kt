package com.capstone.jarvice.ui.bottomNavigation.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.jarvice.R
import com.capstone.jarvice.adapter.JobBannerAdapter
import com.capstone.jarvice.adapter.ListJobAdapter
import com.capstone.jarvice.databinding.FragmentHomeBinding
import com.capstone.jarvice.network.JobsItem
import com.capstone.jarvice.network.ListJobsItem
import com.capstone.jarvice.ui.main.MainActivity
import com.capstone.jarvice.utils.ShowLoading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

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

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("users").child(uid).get().addOnCompleteListener{
            binding.tvWelcomeUsername.text = it.result.child("nameUser").value.toString()
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    homeViewModel.searchJob(p0, this@HomeFragment)
                } else {
                    Toast.makeText(requireContext(), "Masukkan Pencarian", Toast.LENGTH_SHORT).show()
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })


        showViewModel()

    }

    private fun showViewModel() {
        homeViewModel.getBannerJob()
        homeViewModel.getListJob()
        homeViewModel.isLoading.observe(requireActivity()) {
            showLoading.showLoading(it, progressBar)
        }

        homeViewModel.job.observe(requireActivity()) {
            setBannerJob(it)
        }

        homeViewModel.listJob.observe(requireActivity()) {
            setListJob(it)
        }

        homeViewModel.toast.observe(requireActivity()) {
            toast(it)
        }
    }

    private fun setBannerJob(job: List<JobsItem>) {
        val listJobs = ArrayList<JobsItem>()
        for (i in job) {
            val jobBanner = JobsItem(
                i.promopic,
                image = i.image,
                skills = i.skills,
                web = i.web,
                fulltime = i.fulltime,
                name = i.name,
                company = i.company,
                location = i.location,
                category = i.category,
                experience = i.experience,
                salary = i.salary)
            listJobs.addAll(listOf(jobBanner))
        }

        val adapterBanner = JobBannerAdapter(listJobs)
        binding.rvBannerJob.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = adapterBanner
        }
    }

    private fun setListJob(job: List<ListJobsItem>) {
        val listJobs = ArrayList<ListJobsItem>()
        for (i in job) {
            val jobList = ListJobsItem(
                image = i.image,
                skills = i.skills,
                web = i.web,
                fulltime = i.fulltime,
                name = i.name,
                company = i.company,
                location = i.location,
                category = i.category,
                experience = i.experience,
                salary = i.salary
            )
            listJobs.addAll(listOf(jobList))
        }

        val adapterBanner = ListJobAdapter(listJobs)
        binding.rvListJob.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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