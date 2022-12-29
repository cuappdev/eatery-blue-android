package com.cornellappdev.android.eateryblue.ui.components.login

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.cornellappdev.android.eateryblue.R
import es.dmoral.toasty.Toasty

fun showWrongCredentialsToast(context: Context) {
    val wrongCredentialsMessage = "NetID and/or password incorrect, please try again"
    val ssb =
        SpannableStringBuilder(wrongCredentialsMessage)
    ssb.setSpan(
        StyleSpan(Typeface.BOLD),
        0, wrongCredentialsMessage.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )

    Toasty.custom(
        context,
        ssb,
        ContextCompat.getDrawable(context, R.drawable.ic_error),
        ContextCompat.getColor(context, R.color.light_red),
        ContextCompat.getColor(context, R.color.red),
        Toast.LENGTH_SHORT,
        true,
        true
    ).show()
}
