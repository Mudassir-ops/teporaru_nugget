package com.aioapp.nuggetmvp.utils.appextension

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun String.colorizeWordInSentence(wordToColor: String): CharSequence {
    val spannableString = SpannableString(this)
    val colorOrange = Color.parseColor("#EF8549")
    val colorWhite = Color.parseColor("#FFFFFFFF")
    spannableString.setSpan(
        ForegroundColorSpan(colorWhite),
        0,
        this.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    val startIndex = this.indexOf(wordToColor)
    if (startIndex != -1) {
        val endIndex = startIndex + wordToColor.length
        spannableString.setSpan(
            ForegroundColorSpan(colorOrange),
            startIndex,
            endIndex,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannableString
}

fun String.colorizeTwoWordsInSentence(wordToColor: String,secondWordToColor: String): CharSequence {
    val spannableString = SpannableString(this)
    val colorOrange = Color.parseColor("#EF8549")
    val colorWhite = Color.parseColor("#FFFFFFFF")
    spannableString.setSpan(
        ForegroundColorSpan(colorWhite),
        0,
        this.length,
        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    val startIndex = this.indexOf(wordToColor)
    if (startIndex != -1) {
        val endIndex = startIndex + wordToColor.length
        spannableString.setSpan(
            ForegroundColorSpan(colorOrange),
            startIndex,
            endIndex,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    val startIndexForSecond = this.indexOf(secondWordToColor)
    if (startIndexForSecond != -1) {
        val endIndex = startIndexForSecond + secondWordToColor.length
        spannableString.setSpan(
            ForegroundColorSpan(colorOrange),
            startIndexForSecond,
            endIndex,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannableString
}