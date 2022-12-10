package com.capstone.jarvice.ui.bottomNavigation.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.jarvice.R
import com.capstone.jarvice.adapter.ListJobAdapter
import com.capstone.jarvice.databinding.FragmentBookmarkBinding
import com.capstone.jarvice.db.BookmarkJobList
import com.capstone.jarvice.network.ListJobsItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class BookmarkFragment : Fragment() {

    private lateinit var _binding: FragmentBookmarkBinding
    private val binding get() = _binding
    private lateinit var bookmarkViewModel: BookmarkViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentBookmarkBinding.inflate(
            inflater,
            container,
            false)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        FirebaseDatabase.getInstance().reference.child("users").child(uid).get()
            .addOnCompleteListener {
                binding.tvWelcomeUsername.text = it.result.child("nameUser").value.toString()
            }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookmarkViewModel = BookmarkViewModel(requireActivity().application)
        showViewModel()
    }

    private fun showViewModel() {
        bookmarkViewModel.bookmarkList.observe(requireActivity()) {
            showBookmark(it)
        }
    }

    private fun showBookmark(listJobsItems: List<BookmarkJobList>) {
        val job = ArrayList<ListJobsItem>()
        val emptyLayout = view?.findViewById<View>(R.id.empty_layout)
        for (i in listJobsItems) {
            val jobsList = ListJobsItem(
                image = i.image,
                web = i.web,
                fulltime = i.fulltime,
                name = i.jobName,
                company = i.company,
                location = i.location,
                category = i.category,
                experience = i.experience,
            )
            job.add(jobsList)
        }

        if (job.size == 0) {
            emptyLayout?.visibility = View.VISIBLE
        } else {
            emptyLayout?.visibility = View.GONE
        }

        val jobListAdapter = ListJobAdapter(job)
        binding.rvListJob.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = jobListAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = FragmentBookmarkBinding.inflate(layoutInflater)
    }
}