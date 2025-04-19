package com.example.tanks

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.tanks.models.Element
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val KEY_LEVEL = "key_level"

class LevelStorage(context: Context) {
    private val pref = (context as Activity).getPreferences(MODE_PRIVATE)
    private val gson = Gson()
    fun seveLevel(elementOnContainer: List<Element>) {
        pref.edit()
            .putString(KEY_LEVEL, gson.toJson(elementOnContainer))
            .apply()
    }

    fun loadLevel():List<Element>? {
        val levelFromPrefs = pref.getString(KEY_LEVEL, null) ?: return null
        val type = object : TypeToken<List<Element>>() {}.type
        return gson.fromJson(levelFromPrefs, type)

    }
}//