package com.capstone.jarvice.ui.signup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.capstone.jarvice.R
import com.capstone.jarvice.databinding.ActivitySignupBinding
import com.capstone.jarvice.ui.login.Login
import com.google.firebase.auth.FirebaseAuth

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPassword.text.toString()
            val passConfirm = binding.edtPasswordConfirmation.text.toString()

            if (validation.validate()) {
                if (email.isNotEmpty() && pass.isNotEmpty() && passConfirm.isNotEmpty()) {
                    auth.createUserWithEmailAndPassword(email, pass)
                        .addOnCompleteListener {
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

        binding.tvMessageNewSignup.setOnClickListener {
            val intent = Intent(this@SignupActivity, Login::class.java)
            startActivity(intent)
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
}