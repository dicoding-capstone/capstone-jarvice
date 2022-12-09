package com.capstone.jarvice.ui.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.ActivityLoginBinding
import com.capstone.jarvice.model.UserModel
import com.capstone.jarvice.model.UserNetwork
import com.capstone.jarvice.model.UserPreference
import com.capstone.jarvice.ui.ViewModelFactory
import com.capstone.jarvice.ui.main.MainActivity
import com.capstone.jarvice.ui.signup.SignupActivity
import com.capstone.jarvice.utils.ShowLoading
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.database.FirebaseDatabase
import java.util.*


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var showLoading: ShowLoading
    private lateinit var progressBar: View
    private lateinit var callbackManager: CallbackManager
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        FacebookSdk.sdkInitialize(applicationContext)

        // Configure Google Sign In
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()

        showLoading = ShowLoading()
        progressBar = binding.progressBar

        setupView()
        action()
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
            addValidation(binding.edtEmail, Patterns.EMAIL_ADDRESS, getString(R.string.invalid_email))
            addValidation(binding.edtPassword, ".{6,}", getString(R.string.invalid_password))
        }

        binding.button2.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            if (validation.validate()) {
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    showLoading.showLoading(true, progressBar)
                    auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showLoading.showLoading(false, progressBar)
                                loginViewModel.saveUser(UserModel(
                                    isLogin = true,
                                    uidKey = auth.currentUser?.uid
                                ))
                                dialogAlert()
                            } else {
                                showLoading.showLoading(false, progressBar)
                                Toast.makeText(
                                    this,
                                    it.exception.toString(),
//                                    getString(R.string.invalid_account),
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    showLoading.showLoading(false, progressBar)
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
            showLoading.showLoading(true, progressBar)
            signInWithGoogle()
        }

//        binding.btLoginFacebook.setOnClickListener {
//            showLoading.showLoading(true, progressBar)
////            signInWithFacebook()
//        }

        binding.tvMessageNew.setOnClickListener {
            val intent = Intent(this@Login, SignupActivity::class.java)
            startActivity(intent)
        }
    }

//    private fun signInWithFacebook() {
//        callbackManager = CallbackManager.Factory.create()
//
//        LoginManager.getInstance().logInWithReadPermissions(this@Login, listOf("email", "public_profile"))
//        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult>{
//            override fun onSuccess(result: LoginResult?) {
//                showLoading.showLoading(false, progressBar)
//                loginViewModel.saveUser(UserModel(isLogin = true))
//                dialogAlert()
//            }
//
//            override fun onCancel() {
//                Log.d("Login Facebook", "Canceled")
//                Toast.makeText(this@Login, "Login Canceled", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onError(error: FacebookException?) {
//                Log.d("Error Login Facebook", error.toString())
//                Toast.makeText(this@Login, "Gagal Login", Toast.LENGTH_SHORT).show()
//            }
//        })
//
////        val loginCallbackManager by lazy { CallbackManager.Factory.create() }
//
////        LoginManager.getInstance().registerCallback(loginCallbackManager, object : FacebookCallback<LoginResult>{
////            override fun onSuccess(result: LoginResult?) {
////                showLoading.showLoading(false, progressBar)
////                loginViewModel.saveUser(UserModel(isLogin = true))
////                dialogAlert()
////            }
////
////            override fun onCancel() {
////                Log.d("Login Facebook", "Canceled")
////
////            }
////
////            override fun onError(error: FacebookException?) {
////                Log.d("Error Login Facebook", error.toString())
////                Toast.makeText(this@Login, "Gagal Login", Toast.LENGTH_SHORT).show()
////            }
////
////        })
//    }

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
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                auth.fetchSignInMethodsForEmail(account.email.toString()).addOnCompleteListener {
                    Log.d("Email Check", it.result.signInMethods?.size.toString())
                    if (it.result.signInMethods?.size == 0){
                        firebaseAuthWithGoogle(account.idToken!!)
                        val user = UserNetwork(
                            nameUser = account.displayName,
                            email = account.email,
                            photoUrl = account.photoUrl.toString(),
                            keahlian = null
                        )
                        val uid = auth.currentUser!!.uid
                        uploadDatabase(uid, user)
                    }
                }
                firebaseAuthWithGoogle(account.idToken!!)
                showLoading.showLoading(false, progressBar)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                showLoading.showLoading(false, progressBar)
                Toast.makeText(this, e.toString(),
                    Toast.LENGTH_SHORT).show()
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

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    loginViewModel.saveUser(UserModel(isLogin = true))
                    showLoading.showLoading(false, progressBar)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    showLoading.showLoading(false, progressBar)
                    Toast.makeText(this, "Login Gagal",
                        Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if (currentUser != null){
            dialogAlert()
        }
    }

    private fun dialogAlert() {
        AlertDialog.Builder(this@Login).apply {
            setTitle(getString(R.string.title_alert_dialog))
            setMessage(getString(R.string.message_alert_dialog_login))
            setPositiveButton(getString(R.string.message_positive_button)) { _, _ ->
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            create()
            show()
        }

    }
}