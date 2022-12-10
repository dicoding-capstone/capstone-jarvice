package com.capstone.jarvice.ui.bottomNavigation.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.jarvice.adapter.ListJobAdapter
import com.capstone.jarvice.databinding.FragmentBookmarkBinding
import com.capstone.jarvice.db.BookmarkJobList
import com.capstone.jarvice.network.ListJobsItem


class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!
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

        for (i in listJobsItems) {
            val jobsList = ListJobsItem(
                image = i.image,
                name = i.company,
                company = i.company
            )
            job.add(jobsList)
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
        _binding = null
    }
}