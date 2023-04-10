package com.example.submissionandroidintermediate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import com.example.submissionandroidintermediate.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding
//    private val regisViewModel: RegisterViewModel by viewModels()
//    private val mainViewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ifClicked()
    }

    private fun ifClicked() {
        binding.seeRegistPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.RegistPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.RetypePassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.RegistPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.RetypePassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }

            // Set selection to end of text
            binding.RegistPassword.text?.let { binding.RegistPassword.setSelection(it.length) }
            binding.RetypePassword.text?.let { binding.RetypePassword.setSelection(it.length) }
        }
    }
}