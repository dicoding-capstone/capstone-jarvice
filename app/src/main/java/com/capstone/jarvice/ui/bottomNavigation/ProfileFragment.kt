package com.capstone.jarvice.ui.bottomNavigation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
                Glide.with(this).load(it.result.child("photoUrl").toString()).error(R.drawable.ic_profile_picture).into(binding.ivProfilePicture)
            }
        }

//        profileViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}