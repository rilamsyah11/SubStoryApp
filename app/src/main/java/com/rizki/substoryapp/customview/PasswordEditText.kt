package com.rizki.substoryapp.customview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rizki.substoryapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {
    private lateinit var toggleButton: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context,
        attrs,
        defStyleAttr) {
        init()
    }

    private fun init() {
        hint = resources.getString(R.string.password)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        imeOptions = EditorInfo.IME_ACTION_DONE
        inputType = 129

        toggleButton =
            ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable

        setCompoundDrawablesWithIntrinsicBounds(null, null, toggleButton, null)
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 != null && p0.toString().isNotEmpty() && p0.length < 6) {
                    error = resources.getString(R.string.warning_password)
                }
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] == null) return false
        val buttonStart: Float
        val buttonEnd: Float
        var isButtonClicked = false

        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            buttonEnd = (toggleButton.intrinsicWidth + paddingStart).toFloat()
            when {
                event.x < buttonEnd -> isButtonClicked = true
            }
        } else {
            buttonStart = (width - paddingEnd - toggleButton.intrinsicWidth).toFloat()
            when {
                event.x >buttonStart -> isButtonClicked = true
            }
        }

        if (!isButtonClicked) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                toggleButton =
                    ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable

                return true
            }
            MotionEvent.ACTION_UP -> {
                toggleButton =
                    ContextCompat.getDrawable(context, R.drawable.ic_password) as Drawable

                inputType = if (inputType == 129) InputType.TYPE_TEXT_VARIATION_PASSWORD
                else 129

                return true
            }
            else -> return true
        }
    }
}