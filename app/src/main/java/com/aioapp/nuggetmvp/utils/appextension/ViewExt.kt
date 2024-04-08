package com.aioapp.nuggetmvp.utils.appextension

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.aioapp.nuggetmvp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun TextView.handleNoneState(context: Context) {
    this@handleNoneState.text = ""
    this@handleNoneState.text = resources.getString(R.string.none_statement)
    this@handleNoneState.setTextColor(ContextCompat.getColor(context, R.color.orange))
}

fun getRandomAlphabet(): Char {
    val alphabet = "abcdefghijklmnopqrstuvwxyz"
    val randomIndex = Random.nextInt(0, alphabet.length)
    return alphabet[randomIndex]
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
        spannableString.setSpan(
            StyleSpan(Typeface.ITALIC),
            startIndex,
            endIndex,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannableString
}



fun String.colorizeTwoWordsInSentence(
    wordToColor: String,
    secondWordToColor: String
): CharSequence {
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
        spannableString.setSpan(
            StyleSpan(Typeface.ITALIC),
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
        spannableString.setSpan(
            StyleSpan(android.graphics.Typeface.ITALIC),
            startIndexForSecond,
            endIndex,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    return spannableString
}