package com.capstone.jarvice.ui.login

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
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
import com.capstone.jarvice.model.UserPreference
import com.capstone.jarvice.ui.ViewModelFactory
import com.capstone.jarvice.ui.main.MainActivity
import com.capstone.jarvice.ui.signup.SignupActivity
import com.capstone.jarvice.utils.ShowLoading
import com.google.firebase.auth.FirebaseAuth

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var showLoading: ShowLoading
    private lateinit var progressBar: View
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
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
            addValidation(binding.edtUsername, Patterns.EMAIL_ADDRESS, getString(R.string.invalid_email))
            addValidation(binding.edtPassword, ".{6,}", getString(R.string.invalid_password))
        }

        binding.button2.setOnClickListener {
            val email = binding.edtUsername.text.toString()
            val pass = binding.edtPassword.text.toString()
            if (validation.validate()) {
                if (email.isNotEmpty() && pass.isNotEmpty()) {
                    showLoading.showLoading(true, progressBar)
                    auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showLoading.showLoading(false, progressBar)
                                loginViewModel.saveUser(UserModel(isLogin = true))
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

        binding.tvMessageNew.setOnClickListener {
            val intent = Intent(this@Login, SignupActivity::class.java)
            startActivity(intent)
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