package com.example.submissionandroidintermediate.customview

import android.content.Context
import android.graphics.Rect
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
    private var isPasswordValid: Boolean = false

    init {
        init(null)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    constructor(context: Context) : super(context) {
        init(null)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
        transformationMethod = PasswordTransformationMethod.getInstance()
    }

    private fun init(attrs: AttributeSet?) {
        // Load drawables for show/hide password icons
        showPasswordIcon = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_off_24)
        hidePasswordIcon = ContextCompat.getDrawable(context, R.drawable.baseline_visibility_24)

        setCompoundDrawablesWithIntrinsicBounds(null, null, if (isPasswordVisible) hidePasswordIcon else showPasswordIcon, null)
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
            val isTouchWithinShowHideIconBounds = when (layoutDirection) {
                View.LAYOUT_DIRECTION_RTL -> event.x < showPasswordIcon!!.intrinsicWidth
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
            showPasswordIcon?.let { setCompoundDrawablesWithIntrinsicBounds(null, null, hidePasswordIcon, null) }
        } else {
            // Hide password
            transformationMethod = PasswordTransformationMethod.getInstance()
            hidePasswordIcon?.let { setCompoundDrawablesWithIntrinsicBounds(null, null, showPasswordIcon, null) }
        }
        invalidate()
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (!focused) {
            validatePassword()
        }
    }

    private fun validatePassword() {
        isPasswordValid = (text?.length ?: 0) >= 6
        error = if (!isPasswordValid) {
            "Password must be at least 6 characters"
        } else {
            null
        }
    }
}

