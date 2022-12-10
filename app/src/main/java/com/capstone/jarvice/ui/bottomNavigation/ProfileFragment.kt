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
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.FragmentExploreBinding
import com.capstone.jarvice.databinding.FragmentProfileBinding
import com.capstone.jarvice.model.UserNetwork
import com.capstone.jarvice.ui.bottomNavigation.editProfile.EditGoogleActivity
import com.capstone.jarvice.ui.bottomNavigation.editProfile.EditProfileActivity
import com.capstone.jarvice.ui.main.MainViewModel
import com.capstone.jarvice.utils.BottomSheet
import com.capstone.jarvice.utils.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {
    private lateinit var _binding: FragmentProfileBinding
    private lateinit var showLoading: LoadingDialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        showLoading = LoadingDialog(requireActivity())
        showLoading.startLoading()

        val uid: String = FirebaseAuth.getInstance().currentUser!!.uid

        FirebaseDatabase.getInstance().reference.child("users").child(uid).get().addOnCompleteListener {
            Log.d("Data User", it.result.toString())
            if (it.isSuccessful){
                binding.emailProfile.text = it.result.child("email").value.toString()
                binding.tvWelcomeUsername.text = it.result.child("nameUser").value.toString()
                val profilePicture: String = it.result.child("photoUrl").value.toString()
                Glide.with(requireContext()).load(profilePicture).centerCrop().error(R.drawable.ic_profile_picture).into(binding.ivProfilePicture)
                showLoading.dismissLoading()
            } else {
                showLoading.dismissLoading()
                Toast.makeText(requireContext(), "Gagal Memuat Data",Toast.LENGTH_SHORT).show()
            }
        }
        binding.editProfile.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("users").child(uid).get().addOnCompleteListener {
                val result = it.result
                val dataUser = UserNetwork(
                    nameUser = result.child("nameUser").value.toString(),
                    email = result.child("email").value.toString(),
                    photoUrl = result.child("photoUrl").value.toString(),
                )
                Log.d("Method Login Check", result.child("method").value.toString())
                if (result.child("method").value == true){
                    Intent(requireContext(), EditProfileActivity::class.java).also {
                        it.putExtra(EditProfileActivity.DATA_USER, dataUser)
                        startActivity(it)
                    }
                } else {
                    Intent(requireContext(), EditGoogleActivity::class.java).also {
                        it.putExtra(EditGoogleActivity.DATA_USER, dataUser)
                        startActivity(it)
                    }
                }
            }
        }
        binding.keahlian.setOnClickListener {
            BottomSheet().show(childFragmentManager, "New Task Tag")

        }
        binding.pengaturan.setOnClickListener {
            startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
        }
        binding.logout.setOnClickListener {
            AlertDialog.Builder(requireActivity()).apply {
                setTitle("Apakah Anda Yakin?")
                setMessage("Apakah anda yakin untuk logout?")
                setPositiveButton("Yakin") {_, _ ->
                    FirebaseAuth.getInstance().signOut()
                    viewModel.logout()
                }
                setNegativeButton("Tidak") {_, _ ->
                    Toast.makeText(requireContext(), "Batal Logout", Toast.LENGTH_LONG).show()
                }
            }.show()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = FragmentProfileBinding.inflate(layoutInflater)
    }
}