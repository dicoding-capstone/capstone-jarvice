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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.bumptech.glide.Glide
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.ActivityEditGoogleBinding
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

class EditGoogleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditGoogleBinding
    private lateinit var currentPhotoPath: String
    private var imageFile: File? = null
    private var imageUri: Uri? = null
    private lateinit var showLoading: LoadingDialog

    private fun allPermissionsGranted() = EditProfileActivity.REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_google)
        binding = ActivityEditGoogleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        showLoading = LoadingDialog(this@EditGoogleActivity)

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                EditProfileActivity.REQUIRED_PERMISSIONS,
                EditProfileActivity.REQUEST_CODE_PERMISSIONS
            )
        }
        getData()
    }

    private fun getData() {
        binding.apply {
            val user = intent?.extras?.getParcelable<UserNetwork>(EditProfileActivity.DATA_USER)
            if (user != null) {
                edtUsername.setText(user.nameUser)
                Glide.with(this@EditGoogleActivity).load(user.photoUrl).centerCrop().circleCrop().error(R.drawable.ic_profile_picture).into(ivProfilePicture)
            }

            btEditPhoto.setOnClickListener {
                showAlertDialog(this@EditGoogleActivity)
            }

            btSave.setOnClickListener {
                showLoading.startLoading()
                val username = binding.edtUsername.text.toString()
                if (username.isNotEmpty()) {
                    if (imageUri != null) {
                        uploadToServer(imageUri!!)
                    } else {
                        uploadToDatabase(username, null)
                        Log.d("Image Uri", "Is Empty")
                    }
                }
            }

            btBatal.setOnClickListener {
                Intent(this@EditGoogleActivity, MainActivity::class.java).also {
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
                "photoUrl" to imgLink
            )
            dbUser.updateChildren(userHash).addOnCompleteListener {
                if (it.isSuccessful) {
                    Intent(this@EditGoogleActivity, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                    showLoading.dismissLoading()
                    Toast.makeText(
                        this@EditGoogleActivity, "Berhasil Mengubah Data", Toast.LENGTH_SHORT
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
            dbUser.updateChildren(userHash).addOnCompleteListener {
                if (it.isSuccessful) {
                    Intent(this@EditGoogleActivity, MainActivity::class.java).also {
                        startActivity(it)
                        finish()
                    }
                    showLoading.dismissLoading()
                    Toast.makeText(
                        this@EditGoogleActivity, "Berhasil Mengubah Data", Toast.LENGTH_SHORT
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
        val ref = FirebaseStorage.getInstance().reference.child("users").child("${FirebaseAuth.getInstance().currentUser!!.email}.jpg")

        ref.putFile(imgUri).addOnCompleteListener{
            if (it.isSuccessful) {
                ref.downloadUrl.addOnCompleteListener { Uri ->
                    val username = binding.edtUsername.text.toString()
                    uploadToDatabase(username, Uri.result.toString())
                }
            }
        }
    }

    private fun showAlertDialog(editGoogleActivity: EditGoogleActivity) {
        AlertDialog.Builder(editGoogleActivity).apply {
            setTitle("Pilh Gambar")
            setMessage("Silahkan pilih dari mana anda ingin mengambil data")
            setPositiveButton("Camera") {_, _ ->
                startTakePhoto()
            }
            setNegativeButton("Galery") {_, _ ->
                startIntentGalery()
            }
        }.create().show()
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
            val myFile = uriToFile(selectedImg, this@EditGoogleActivity)
            imageFile = myFile
            imageUri = selectedImg
            Glide.with(this).load(selectedImg).centerCrop().circleCrop().error(R.drawable.ic_profile_picture).into(binding.ivProfilePicture)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@EditGoogleActivity,
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
            Glide.with(this).asBitmap().load(result).circleCrop().error(R.drawable.ic_profile_picture).into(binding.ivProfilePicture)
        }
    }

    companion object {
        const val DATA_USER = "data_user"
    }
}