package com.cornellappdev.android.eateryblue.ui.components.login

import android.content.Context
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.core.content.ContextCompat
import es.dmoral.toasty.Toasty

fun LoginToast(context: Context, message: String, icon: Int, color: Int, borderColor: Int) {
    val ssb =
        SpannableStringBuilder(message)
    ssb.setSpan(
        StyleSpan(Typeface.BOLD),
        0, message.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE
    )

    Toasty.custom(
        context,
        ssb,
        ContextCompat.getDrawable(context, icon),
        ContextCompat.getColor(context, color),
        ContextCompat.getColor(context, borderColor),
        Toast.LENGTH_SHORT,
        true,
        true
    ).show()
}