package com.jdh.calendar_kt

import android.content.Context
import android.content.SharedPreferences


class PreferenceManager {
    private val PREFERENCES_NAME = "rebuild_preference"
    private val DEFAULT_VALUE_STRING = ""
    private val DEFAULT_VALUE_BOOLEAN = false
    private val DEFAULT_VALUE_INT = -1
    private val DEFAULT_VALUE_LONG = -1L
    private val DEFAULT_VALUE_FLOAT = -1f

    private fun getPreferences(context: Context, preferenceName: String): SharedPreferences {
        return context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
    }

    /**
     *
     * String 값 저장
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setString(context: Context, preferenceName: String, key: String?, value: String?) {
        val prefs = getPreferences(context, preferenceName)
        val editor = prefs.edit()
        editor.putString(key, value)
        editor.commit()
    }

    /**
     *
     * boolean 값 저장
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setBoolean(context: Context, preferenceName: String, key: String?, value: Boolean) {
        val prefs = getPreferences(context, preferenceName)
        val editor = prefs.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }

    /**
     *
     * int 값 저장
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setInt(context: Context, preferenceName: String, key: String?, value: Int) {
        val prefs = getPreferences(context, preferenceName)
        val editor = prefs.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    /**
     *
     * long 값 저장
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setLong(context: Context, preferenceName: String, key: String?, value: Long) {
        val prefs = getPreferences(context, preferenceName)
        val editor = prefs.edit()
        editor.putLong(key, value)
        editor.commit()
    }

    /**
     *
     * float 값 저장
     *
     * @param context
     *
     * @param key
     *
     * @param value
     */
    fun setFloat(context: Context, preferenceName: String, key: String?, value: Float) {
        val prefs = getPreferences(context, preferenceName)
        val editor = prefs.edit()
        editor.putFloat(key, value)
        editor.commit()
    }

    /**
     *
     * String 값 로드
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getString(context: Context, preferenceName: String, key: String?): String? {
        val prefs = getPreferences(context, preferenceName)
        return prefs.getString(key, DEFAULT_VALUE_STRING)
    }

    /**
     *
     * boolean 값 로드
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getBoolean(context: Context, preferenceName: String, key: String?): Boolean {
        val prefs = getPreferences(context, preferenceName)
        return prefs.getBoolean(key, DEFAULT_VALUE_BOOLEAN)
    }

    /**
     *
     * int 값 로드
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getInt(context: Context, preferenceName: String, key: String?): Int {
        val prefs = getPreferences(context, preferenceName)
        return prefs.getInt(key, DEFAULT_VALUE_INT)
    }

    /**
     *
     * long 값 로드
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getLong(context: Context, preferenceName: String, key: String?): Long {
        val prefs = getPreferences(context, preferenceName)
        return prefs.getLong(key, DEFAULT_VALUE_LONG)
    }

    /**
     *
     * float 값 로드
     *
     * @param context
     *
     * @param key
     *
     * @return
     */
    fun getFloat(context: Context, preferenceName: String, key: String?): Float {
        val prefs = getPreferences(context, preferenceName)
        return prefs.getFloat(key, DEFAULT_VALUE_FLOAT)
    }

    /**
     *
     * 키 값 삭제
     *
     * @param context
     *
     * @param key
     */
    fun removeKey(context: Context, preferenceName: String, key: String?) {
        val prefs = getPreferences(context, preferenceName)
        val edit = prefs.edit()
        edit.remove(key)
        edit.commit()
    }

    /**
     *
     * 모든 저장 데이터 삭제
     *
     * @param context
     */
    fun clear(context: Context, preferenceName: String) {
        val prefs = getPreferences(context, preferenceName)
        val edit = prefs.edit()
        edit.clear()
        edit.commit()
    }
}