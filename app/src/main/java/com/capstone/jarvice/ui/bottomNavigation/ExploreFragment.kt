package com.capstone.jarvice.ui.bottomNavigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.jarvice.adapter.JobBannerAdapter
import com.capstone.jarvice.adapter.ListJobAdapter
import com.capstone.jarvice.databinding.FragmentExploreBinding
import com.capstone.jarvice.network.JobsItem
import com.capstone.jarvice.network.ListJobsItem
import com.capstone.jarvice.ui.bottomNavigation.home.HomeViewModel
import com.google.firebase.database.FirebaseDatabase


class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val homeViewModel: HomeViewModel by viewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
//        val exploreViewModel =
//            ViewModelProvider(this).get(ExploreViewModel::class.java)

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.searchViewExplore.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchJob(query!!)
                return if (query != null) {
                    searchJob(query)
                    false
                } else{
                    Toast.makeText(requireContext(), "Masukkan Karakter",Toast.LENGTH_SHORT).show()
                    false
                }
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null) {
                    searchJob(query)
                    return false
                } else {
                    return false
                }
            }
        })

        homeViewModel.jobSearch.observe(requireActivity()) {
            search(it)
        }

        return root
    }

    private fun search(search: String?) {
        if (search != null) {
            binding.searchViewExplore.setQuery(search, false)
        }

    }

    private fun searchJob(job: String) {
        homeViewModel.getSearchJob(job)
        homeViewModel.listSearch.observe(requireActivity()) {
            Log.d("List Search 2", it.toString())
            setBannerJob(it)
        }
    }

    private fun setBannerJob(job: List<ListJobsItem>) {
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

        Log.d("Data Adapter Search", listJobs.toString())

        val adapterBanner = ListJobAdapter(listJobs)
        binding.rvListJob.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = adapterBanner
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}