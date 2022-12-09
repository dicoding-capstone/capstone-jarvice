package com.capstone.jarvice.ui.bottomNavigation.editProfile

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.bumptech.glide.Glide
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.ActivityEditProfileBinding
import com.capstone.jarvice.databinding.ActivityMainBinding
import com.capstone.jarvice.model.UserNetwork
import com.capstone.jarvice.ui.bottomNavigation.ProfileFragment
import com.capstone.jarvice.ui.main.MainActivity
import com.capstone.jarvice.utils.LoadingDialog
import com.capstone.jarvice.utils.createCustomTempFile
import com.capstone.jarvice.utils.uriToFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var currentPhotoPath: String
    private var imageFile: File? = null
    private var imageUri: Uri? = null
    private lateinit var showLoading: LoadingDialog

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading = LoadingDialog(this@EditProfileActivity)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        getData()
    }

    private fun getData() {
        binding.apply {
            val user = intent?.extras?.getParcelable<UserNetwork>(DATA_USER)
            if (user != null) {
                edtUsername.setText(user.nameUser)
                edtEmail.setText(user.email)
                Glide.with(this@EditProfileActivity).load(user.photoUrl).centerCrop().circleCrop().error(R.drawable.ic_profile_picture).into(ivProfilePicture)
            }

            cvImage.setOnClickListener {
                AlertDialog.Builder(this@EditProfileActivity).apply {
                    setTitle("Pilh Gambar")
                    setMessage("Silahkan pilih dari mana anda ingin mengambil data")
                    setPositiveButton("Camera") {_, _ ->
                        startTakePhoto()
                    }
                    setNegativeButton("Galery") {_, _ ->
                        startIntentGalery()
                    }
                }.show()
            }

            btSave.setOnClickListener {
                val validation = AwesomeValidation(ValidationStyle.BASIC)
                validation.apply {
                    addValidation(binding.edtEmail, Patterns.EMAIL_ADDRESS, getString(R.string.invalid_email))
                    addValidation(binding.edtPassword, ".{6,}", getString(R.string.invalid_password))
                }

                showLoading.startLoading()
                val email = binding.edtEmail.text.toString()
                val password = binding.edtPasswordConfirmation.text.toString()
                val username = binding.edtUsername.text.toString()
                if (email.isNotEmpty() && password.isNotEmpty() && validation.validate()) {
                    if (imageUri != null) {
                        uploadToServer(imageUri!!)
                    } else {
                        uploadToDatabase(username, null)
                        Log.d("Image Uri", "Is Empty")
                    }
                } else {
                    Toast.makeText(
                        this@EditProfileActivity,
                        getString(R.string.invalid_validation),
                        Toast.LENGTH_SHORT).show()
                }
            }

            btBatal.setOnClickListener {
                Intent(this@EditProfileActivity, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }
    }

    private fun uploadToDatabase(username: String, imgLink: String?) {
        if (imgLink != null) {
            val dbUser = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            val userHash = hashMapOf<String, Any>(
                "nameUser" to username,
                "photoUrl" to imgLink,
            )
            FirebaseAuth.getInstance().currentUser!!.updateEmail(binding.edtEmail.text.toString())
            FirebaseAuth.getInstance().currentUser!!.updatePassword(binding.edtPasswordConfirmation.text.toString())
            dbUser.updateChildren(userHash).addOnCompleteListener {
                if (it.isSuccessful) {
                    Intent(this@EditProfileActivity, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                    showLoading.dismissLoading()
                    Toast.makeText(
                        this@EditProfileActivity, "Berhasil Mengubah Data", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showLoading.dismissLoading()
                    Toast.makeText(
                        this,
                        "Gagal Mengubah Data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            val dbUser = FirebaseDatabase.getInstance().reference.child("users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            val userHash = hashMapOf<String, Any>(
                "nameUser" to username,
            )
            FirebaseAuth.getInstance().currentUser!!.updateEmail(binding.edtEmail.text.toString())
            FirebaseAuth.getInstance().currentUser!!.updatePassword(binding.edtPasswordConfirmation.text.toString())

            dbUser.updateChildren(userHash).addOnCompleteListener {
                if (it.isSuccessful) {
                    Intent(this@EditProfileActivity, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                    showLoading.dismissLoading()
                    Toast.makeText(
                        this@EditProfileActivity, "Berhasil Mengubah Data", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showLoading.dismissLoading()
                    Toast.makeText(
                        this,
                        "Gagal Mengubah Data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun uploadToServer(imgUri: Uri) {
        val ref = FirebaseStorage.getInstance().reference.child("users").child("${FirebaseAuth.getInstance().currentUser!!.email}")

        ref.putFile(imgUri).addOnCompleteListener{
            if (it.isSuccessful) {
                ref.downloadUrl.addOnCompleteListener { Uri ->
                    val username = binding.edtUsername.text.toString()
                    uploadToDatabase(username, Uri.result.toString())
                }
            }
        }
    }

    private fun startIntentGalery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@EditProfileActivity)
            imageFile = myFile
            imageUri = selectedImg
            Glide.with(this).load(selectedImg).circleCrop().error(R.drawable.ic_profile_picture).into(binding.ivProfilePicture)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@EditProfileActivity,
                "com.capstone.jarvice.ui.bottomNavigation.editProfile",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            val result =  BitmapFactory.decodeFile(myFile.path)
            imageFile = myFile
            imageUri = Uri.fromFile(myFile)
            Glide.with(this).load(result).centerCrop().circleCrop().error(R.drawable.ic_profile_picture).into(binding.ivProfilePicture)
        }
    }

    companion object {
        const val DATA_USER = "data_user"
        val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val REQUEST_CODE_PERMISSIONS = 10
    }
}