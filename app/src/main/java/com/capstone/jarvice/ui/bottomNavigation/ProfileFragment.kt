package com.capstone.jarvice.ui.bottomNavigation

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.FragmentProfileBinding
import com.capstone.jarvice.ui.main.MainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val uid: String = FirebaseAuth.getInstance().currentUser!!.uid
        Log.d("UID User Profile", uid)

        FirebaseDatabase.getInstance().reference.child("users").child(uid).get().addOnCompleteListener {
            Log.d("Data User", it.result.toString())
            if (it.isSuccessful){
                binding.emailProfile.text = it.result.child("email").value.toString()
                binding.tvWelcomeUsername.text = it.result.child("nameUser").value.toString()
                val profilePicture: String = it.result.child("photoUrl").value.toString()
                Glide.with(requireContext()).load(profilePicture).timeout(1000).error(R.drawable.ic_profile_picture).into(binding.ivProfilePicture)
            }
        }
        binding.pengaturan.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.logout.setOnClickListener {
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Apakah Anda Yakin?")
                setMessage("Apakah anda yakin untuk logout?")
                setPositiveButton("Yakin") {_, _ ->
                    FirebaseAuth.getInstance().signOut()
                    viewModel.logout()
                }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}