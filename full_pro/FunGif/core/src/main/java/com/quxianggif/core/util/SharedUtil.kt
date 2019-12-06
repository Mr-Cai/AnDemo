/*
 * Copyright (C) guolin, Suzhou Quxiang Inc. Open source codes for study only.
 * Do not use for commercial purpose.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quxianggif.core.util

import androidx.preference.PreferenceManager
import com.quxianggif.core.GifFun

/**
 * SharedPreferences工具类，提供简单的封装接口，简化SharedPreferences的用法。
 *
 * @author guolin
 * @since 17/2/15
 */
object SharedUtil {
    /**
     * 存储boolean类型的键值对到SharedPreferences文件当中。
     *
     * @param key   存储的键
     * @param value 存储的值
     */
    fun save(key: String?, value: Boolean) {
        val editor = PreferenceManager.getDefaultSharedPreferences(
            GifFun.context!!
        ).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    /**
     * 存储float类型的键值对到SharedPreferences文件当中。
     *
     * @param key   存储的键
     * @param value 存储的值
     */
    fun save(key: String?, value: Float) {
        val editor = PreferenceManager.getDefaultSharedPreferences(
            GifFun.context!!
        ).edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    /**
     * 存储int类型的键值对到SharedPreferences文件当中。
     *
     * @param key   存储的键
     * @param value 存储的值
     */
    fun save(key: String?, value: Int) {
        val editor = PreferenceManager.getDefaultSharedPreferences(
            GifFun.context!!
        ).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    /**
     * 存储long类型的键值对到SharedPreferences文件当中。
     *
     * @param key   存储的键
     * @param value 存储的值
     */
    fun save(key: String?, value: Long) {
        val editor = PreferenceManager.getDefaultSharedPreferences(
            GifFun.context!!
        ).edit()
        editor.putLong(key, value)
        editor.apply()
    }

    /**
     * 存储String类型的键值对到SharedPreferences文件当中。
     *
     * @param key   存储的键
     * @param value 存储的值
     */
    fun save(key: String?, value: String?) {
        val editor = PreferenceManager.getDefaultSharedPreferences(
            GifFun.context!!
        ).edit()
        editor.putString(key, value)
        editor.apply()
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的boolean类型的值。
     *
     * @param key      读取的键
     * @param defValue 如果读取不到值，返回的默认值
     * @return boolean类型的值，如果读取不到，则返回默认值
     */
    fun read(key: String?, defValue: Boolean): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(GifFun.context!!)
        return prefs.getBoolean(key, defValue)
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的float类型的值。
     *
     * @param key      读取的键
     * @param defValue 如果读取不到值，返回的默认值
     * @return float类型的值，如果读取不到，则返回默认值
     */
    fun read(key: String?, defValue: Float): Float {
        val prefs = PreferenceManager.getDefaultSharedPreferences(GifFun.context!!)
        return prefs.getFloat(key, defValue)
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的int类型的值。
     *
     * @param key      读取的键
     * @param defValue 如果读取不到值，返回的默认值
     * @return int类型的值，如果读取不到，则返回默认值
     */
    fun read(key: String?, defValue: Int): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(GifFun.context!!)
        return prefs.getInt(key, defValue)
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的long类型的值。
     *
     * @param key      读取的键
     * @param defValue 如果读取不到值，返回的默认值
     * @return long类型的值，如果读取不到，则返回默认值
     */
    fun read(key: String?, defValue: Long): Long {
        val prefs = PreferenceManager.getDefaultSharedPreferences(GifFun.context!!)
        return prefs.getLong(key, defValue)
    }

    /**
     * 从SharedPreferences文件当中读取参数传入键相应的String类型的值。
     *
     * @param key      读取的键
     * @param defValue 如果读取不到值，返回的默认值
     * @return String类型的值，如果读取不到，则返回默认值
     */
    fun read(key: String?, defValue: String?): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(GifFun.context!!)
        return prefs.getString(key, defValue)!!
    }

    /**
     * 判断SharedPreferences文件当中是否包含指定的键值。
     *
     * @param key 判断键是否存在
     * @return 键已存在返回true，否则返回false。
     */
    operator fun contains(key: String?): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(GifFun.context!!)
        return prefs.contains(key)
    }

    /**
     * 清理SharedPreferences文件当中传入键所对应的值。
     *
     * @param key 想要清除的键
     */
    fun clear(key: String?) {
        val editor = PreferenceManager.getDefaultSharedPreferences(
            GifFun.context!!
        ).edit()
        editor.remove(key)
        editor.apply()
    }

    /**
     * 将SharedPreferences文件中存储的所有值清除。
     */
    fun clearAll() {
        val editor = PreferenceManager.getDefaultSharedPreferences(
            GifFun.context!!
        ).edit()
        editor.clear()
        editor.apply()
    }
}