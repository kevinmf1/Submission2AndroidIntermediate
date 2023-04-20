package com.example.submissionandroidintermediate.userinterface

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.submissionandroidintermediate.R
import com.example.submissionandroidintermediate.UserPreferences
import com.example.submissionandroidintermediate.databinding.ActivityRegistrationBinding
import com.example.submissionandroidintermediate.dataclass.LoginDataAccount
import com.example.submissionandroidintermediate.dataclass.RegisterDataAccount
import com.example.submissionandroidintermediate.viewmodel.MainViewModel
import com.example.submissionandroidintermediate.viewmodel.UserLoginViewModel
import com.example.submissionandroidintermediate.viewmodel.ViewModelFactory

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.createAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ifClicked()

        // access dataStore from UserPreferences and when login session is true, go to HomePageActivity and finish this activity
        val pref = UserPreferences.getInstance(dataStore)
        val userLoginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[UserLoginViewModel::class.java]
        userLoginViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@RegistrationActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        mainViewModel.messageRegist.observe(this) { messageRegist ->
            responseRegister(
                mainViewModel.isErrorRegist,
                messageRegist
            )
        }

        mainViewModel.isLoadingRegist.observe(this) {
            showLoading(it)
        }

        mainViewModel.messageLogin.observe(this) { messageLogin ->
            responseLogin(
                mainViewModel.isErrorLogin,
                messageLogin,
                userLoginViewModel
            )
        }

        mainViewModel.isLoadingLogin.observe(this) {
            showLoading(it)
        }
    }

    private fun responseLogin(
        isError: Boolean,
        message: String,
        userLoginViewModel: UserLoginViewModel
    ) {
        if (!isError) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = mainViewModel.userLogin.value
            userLoginViewModel.saveLoginSession(true)
            userLoginViewModel.saveToken(user?.loginResult!!.token)
            userLoginViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun responseRegister(
        isError: Boolean,
        message: String,
    ) {
        if (!isError) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val userLogin = LoginDataAccount(
                binding.RegistEmail.text.toString(),
                binding.RegistPassword.text.toString()
            )
            mainViewModel.getResponseLogin(userLogin)
        } else {
            if (message == "1") {
                binding.RegistEmail.setErrorMessage(resources.getString(R.string.emailTaken), binding.RegistEmail.text.toString())
                Toast.makeText(this, resources.getString(R.string.emailTaken), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ifClicked() {
        binding.seeRegistPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.RegistPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.RetypePassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.RegistPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.RetypePassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }

            // Set selection to end of text
            binding.RegistPassword.text?.let { binding.RegistPassword.setSelection(it.length) }
            binding.RetypePassword.text?.let { binding.RetypePassword.setSelection(it.length) }
        }

        binding.btnRegistAccount.setOnClickListener {
            binding.apply {
                RegistName.clearFocus()
                RegistEmail.clearFocus()
                RegistPassword.clearFocus()
                RetypePassword.clearFocus()
            }

            if (binding.RegistName.isNameValid && binding.RegistEmail.isEmailValid && binding.RegistPassword.isPasswordValid && binding.RetypePassword.isPasswordValid) {
                val dataRegisterAccount = RegisterDataAccount(
                    name = binding.RegistName.text.toString().trim(),
                    email = binding.RegistEmail.text.toString().trim(),
                    password = binding.RegistPassword.text.toString().trim()
                )

                mainViewModel.getResponseRegister(dataRegisterAccount)
            } else {
                if (!binding.RegistName.isNameValid) binding.RegistName.error =
                    resources.getString(R.string.nameNone)
                if (!binding.RegistEmail.isEmailValid) binding.RegistEmail.error =
                    resources.getString(R.string.emailNone)
                if (!binding.RegistPassword.isPasswordValid) binding.RegistPassword.error =
                    resources.getString(R.string.passwordNone)
                if (!binding.RetypePassword.isPasswordValid) binding.RetypePassword.error =
                    resources.getString(R.string.passwordConfirmNone)

                Toast.makeText(this, R.string.invalidLogin, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}