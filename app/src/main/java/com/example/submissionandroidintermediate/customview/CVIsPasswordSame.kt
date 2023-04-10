package com.example.submissionandroidintermediate.customview

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.submissionandroidintermediate.R

class CVIsPasswordSame : AppCompatEditText, View.OnFocusChangeListener {

    private var isPasswordValid = false

    init {
        init()
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Set background with border
        background = ContextCompat.getDrawable(context, R.drawable.border)
        transformationMethod = PasswordTransformationMethod.getInstance()

        // Set onFocusChangeListener to validate password
        onFocusChangeListener = this
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (!hasFocus) {
            validatePassword()
        }
    }

    private fun validatePassword() {
        val password = text.toString().trim()
        val confirmPassword = (parent as ViewGroup).findViewById<CVPassword>(R.id.RegistPassword).text.toString().trim()

        isPasswordValid = password.length >= 6 && password == confirmPassword
        error = if (!isPasswordValid) {
            resources.getString(R.string.passwordNotMatch)
        } else {
            null
        }
    }
}
