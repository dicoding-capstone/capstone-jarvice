package com.capstone.jarvice.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.FragmentBottomSheetBinding
import com.capstone.jarvice.model.UserNetwork
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetBinding.inflate(inflater, container, false)

        binding.apply {
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            FirebaseDatabase.getInstance().reference.child("users").child(uid).get().addOnCompleteListener {
                if (it.isSuccessful && it.result.child("keahlian").value != null) {
                    binding.descriptionEditText.setText(it.result.child("keahlian").value.toString())
                }
            }

            btSave.setOnClickListener{
                val skill = descriptionEditText.text.toString()
                val user = hashMapOf<String, Any>(
                    "keahlian" to skill
                )
                val dbUser = FirebaseDatabase.getInstance().reference.child("users").child(uid)
                dbUser.updateChildren(user).addOnCompleteListener {
                    if (it.isSuccessful) {
                        Toast.makeText(requireContext(), "Berhasil Update Data", Toast.LENGTH_SHORT).show()
                        dismiss()
                    } else {
                        Toast.makeText(requireContext(), "Gagal Update Data", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        return binding.root
    }

}