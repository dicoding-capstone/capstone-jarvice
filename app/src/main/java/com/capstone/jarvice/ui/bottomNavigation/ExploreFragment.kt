package com.capstone.jarvice.ui.bottomNavigation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.jarvice.adapter.ListJobAdapter
import com.capstone.jarvice.databinding.FragmentExploreBinding
import com.capstone.jarvice.network.ListJobsItem
import com.capstone.jarvice.ui.bottomNavigation.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ExploreFragment : Fragment() {

    private lateinit var _binding: FragmentExploreBinding
    private val binding get() = _binding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root

        search()

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("users").child(uid).get().addOnCompleteListener{
            binding.tvWelcomeUsername.text = it.result.child("nameUser").value.toString()
        }

        searchJob(null)

        binding.searchViewExplore.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null){
                    searchJob(query)
                }
                return false
            }

            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextChange(query: String?): Boolean {
                if (query != null){
                    searchJob(query)
                }
                return false
            }
        })

        return root
    }

    private fun search() {
        val args = this.arguments
        findNavController().currentDestination?.arguments?.get("SEARCH")
        val searchHome = args?.get("SEARCH")
        if (searchHome != null){
            Log.d("Search Home", searchHome.toString())
            binding.searchViewExplore.setQuery(searchHome.toString(), false)
            searchJob(searchHome.toString())
        } else {
            searchJob("A")
        }
    }

    private fun searchJob(search: String?) {
        if (search != null){
            homeViewModel.firebaseSearch(search)
            Log.d("List Search Ch", homeViewModel.listSearch.value.toString())
            homeViewModel.listSearch.observe(requireActivity()) {
                setBannerJob(it)
            }
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
        _binding = FragmentExploreBinding.inflate(layoutInflater)
    }
}