package com.example.submissionandroidintermediate.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.submissionandroidintermediate.R
class CVPassword : AppCompatEditText, View.OnTouchListener {

    private var isPasswordVisible = false
    private var showPasswordIcon: Drawable? = null
    private var hidePasswordIcon: Drawable? = null
    private var passwordVisibilityToggleEnabled: Boolean = true

    init {
        init(null)
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        // Load drawables for show/hide password icons
        showPasswordIcon = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24)
        hidePasswordIcon = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_off_24)

        // Load attributes from XML
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.CVPassword)
            passwordVisibilityToggleEnabled = typedArray.getBoolean(R.styleable.CVPassword_passwordVisibilityToggleEnabled, true)
            typedArray.recycle()
        }

        // Set onTouchListener to handle show/hide password icon clicks
        setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (passwordVisibilityToggleEnabled && event.action == MotionEvent.ACTION_UP) {
            // Check if touch event occurred within the bounds of the show/hide password icon
            val isTouchWithinShowHideIconBounds = when {
                layoutDirection == View.LAYOUT_DIRECTION_RTL -> event.x < showPasswordIcon!!.intrinsicWidth
                else -> event.x > width - showPasswordIcon!!.intrinsicWidth
            }

            if (isTouchWithinShowHideIconBounds) {
                isPasswordVisible = !isPasswordVisible
                updatePasswordVisibility()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun updatePasswordVisibility() {
        if (isPasswordVisible) {
            // Show password
            transformationMethod = null
            showPasswordIcon?.let { setButtonDrawables(it) }
        } else {
            // Hide password
            transformationMethod = PasswordTransformationMethod.getInstance()
            hidePasswordIcon?.let { setButtonDrawables(it) }
        }
    }

    private fun setButtonDrawables(endOfTheText: Drawable) {
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, endOfTheText, null)
    }

}