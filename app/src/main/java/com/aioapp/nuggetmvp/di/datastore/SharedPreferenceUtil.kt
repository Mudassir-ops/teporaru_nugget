package com.aioapp.nuggetmvp.di.datastore

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

object SharedPreferenceUtil {
    private const val NAME = "__arif"
    private const val MODE = Context.MODE_PRIVATE
    private lateinit var preferences: SharedPreferences

    private val CART_ITEM_COUNT = Pair("CART_ITEM_COUNT", "0")

    fun init(context: Application) {
        preferences = context.getSharedPreferences(NAME, MODE)
    }

    /**
     * SharedPreferences extension function, so we won't need to call edit()
    and apply()
     * ourselves on every SharedPreferences operation.
     */
    private inline fun SharedPreferences.edit(
        operation:
            (SharedPreferences.Editor) -> Unit
    ) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }


    var savedCartItemsCount: String?
        get() = preferences.getString(CART_ITEM_COUNT.first, CART_ITEM_COUNT.second)
        set(value) = preferences.edit {
            it.putString(CART_ITEM_COUNT.first, value)
        }

    fun saveBoolean(key: String, value: Boolean) {
        preferences.edit {
            it.putBoolean(key, value)
        }
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences.getBoolean(key, defaultValue)
    }

    fun hasValue(toString: String): Boolean {
        return preferences.contains(toString)
    }

}