package com.conamobile.lotin.memory

import android.content.Context

class MySharedPreferences(context: Context) {
    private val pref = context.getSharedPreferences("shared", Context.MODE_PRIVATE)

    fun isSavedTextSize(isSavedTextSize: Int) {
        val editor = pref.edit()
        editor.putInt("isSavedTextSize", isSavedTextSize)
        editor.apply()
    }

    fun getSavedTextSize(): Int {
        return pref.getInt("isSavedTextSize", 3)
    }

    fun isSavedSwitcher(isSavedSwitcher: Boolean) {
        val editor = pref.edit()
        editor.putBoolean("isSavedSwitcher", isSavedSwitcher)
        editor.apply()
    }

    fun getSavedSwitcher(): Boolean {
        return pref.getBoolean("isSavedSwitcher", false)
    }
}