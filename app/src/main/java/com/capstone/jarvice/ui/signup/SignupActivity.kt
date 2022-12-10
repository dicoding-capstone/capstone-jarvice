package com.capstone.jarvice.ui.signup

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.ActivitySignupBinding
import com.capstone.jarvice.model.UserNetwork
import com.capstone.jarvice.ui.login.Login
import com.capstone.jarvice.utils.LoadingDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseDatabase
    private lateinit var showLoading: LoadingDialog
    private lateinit var progressBar: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        showLoading = LoadingDialog(this)
        progressBar = binding.progressBar

        setupView()
        action()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun action() {
        val validation = AwesomeValidation(ValidationStyle.BASIC)
        validation.apply {
            addValidation(binding.edtEmail,
                Patterns.EMAIL_ADDRESS,
                getString(R.string.invalid_email))
            addValidation(binding.edtPassword, ".{6,}", getString(R.string.invalid_password))
            addValidation(binding.edtPasswordConfirmation,
                binding.edtPassword,
                getString(R.string.invalid_confirmation_password))
        }

        binding.button2.setOnClickListener {
            val name = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString().trim()
            val pass = binding.edtPassword.text.toString().trim()
            val passConfirm = binding.edtPasswordConfirmation.text.toString()

            if (validation.validate()) {
                if (email.isNotEmpty() && pass.isNotEmpty() && passConfirm.isNotEmpty()) {
                    showLoading.startLoading()
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showLoading.dismissLoading()
                                val dbUser = db.reference.child("users").child(auth.currentUser!!.uid)
                                val user = UserNetwork(
                                    nameUser = name,
                                    email = email,
                                    photoUrl = null,
                                    keahlian = null,
                                    method = true
                                )
                                dbUser.setValue(user).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        dialogAlert()
                                    } else {
                                        Toast.makeText(
                                            this,
                                            it.exception.toString(),
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                showLoading.dismissLoading()
                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    showLoading.dismissLoading()
                    Toast.makeText(
                        this,
                        getString(R.string.empty_field),
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.invalid_validation),
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.btLoginGoogle.setOnClickListener {
            showLoading.startLoading()
            signInWithGoogle()
        }

        binding.tvMessageNewSignup.setOnClickListener {
            val intent = Intent(this@SignupActivity, Login::class.java)
            startActivity(intent)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        resultLauncher.launch(signInIntent)
    }

    private var resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(ContentValues.TAG, "firebaseAuthWithGoogle:" + account.id)
                auth.fetchSignInMethodsForEmail(account.email.toString()).addOnCompleteListener {
                    Log.d("Email Check", it.result.signInMethods?.size.toString())
                    if (it.result.signInMethods?.size == 0){
                        val user = UserNetwork(
                            nameUser = account.displayName,
                            email = account.email,
                            photoUrl = account.photoUrl.toString(),
                            keahlian = null,
                            method = false
                        )
                        firebaseAuthWithGoogle(account.idToken!!, user)
                    } else {
                        Toast.makeText(
                            this,
                            "Account Is Already Registered",
                            Toast.LENGTH_SHORT).show()
                    }
                }
                showLoading.dismissLoading()
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(ContentValues.TAG, "Google sign in failed", e)
                showLoading.dismissLoading()
                Toast.makeText(this, e.toString(),
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String, dataUpload: UserNetwork?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    if (dataUpload != null){
                        uploadDatabase(user!!.uid, dataUpload)
                    }
                    showLoading.dismissLoading()
                    dialogAlert()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    showLoading.dismissLoading()
                    Toast.makeText(this, task.exception.toString(),
                        Toast.LENGTH_SHORT).show()
                    dialogAlert()
                }
            }
    }

    private fun uploadDatabase(uid: String, user: UserNetwork) {
        val dbUser = db.reference.child("users").child(uid)
        dbUser.setValue(user).addOnCompleteListener {
            if (it.isSuccessful) {
                dialogAlert()
            } else {
                Toast.makeText(
                    this,
                    "Gagal Save Ke Database",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun dialogAlert() {
        AlertDialog.Builder(this@SignupActivity).apply {
            setTitle(getString(R.string.title_alert_dialog))
            setMessage(getString(R.string.message_alert_dialog))
            setPositiveButton(getString(R.string.message_positive_button)) { _, _ ->
                val intent = Intent(this@SignupActivity, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.logoJarvice, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()
    }
}