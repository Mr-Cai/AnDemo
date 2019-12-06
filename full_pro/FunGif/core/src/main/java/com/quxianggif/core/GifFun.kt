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
package com.quxianggif.core

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.quxianggif.core.util.SharedUtil.clear
import com.quxianggif.core.util.SharedUtil.read

/**
 * 全局的API接口。
 *
 * @author guolin
 * @since 17/2/15
 */
object GifFun {
    var isDebug = false
    /**
     * 获取全局Context，在代码的任意位置都可以调用，随时都能获取到全局Context对象。
     *
     * @return 全局Context对象。
     */
    @SuppressLint("StaticFieldLeak")
    var context: Context? = null
        private set
    /**
     * 获取创建在主线程上的Handler对象。
     *
     * @return 创建在主线程上的Handler对象。
     */
    var handler: Handler? = null
        private set
    /**
     * 判断用户是否已登录。
     *
     * @return 已登录返回true，未登录返回false。
     */
    var isLogin = false
        private set
    /**
     * 获取当前登录用户的id。
     * @return 当前登录用户的id。
     */
    var userId: Long = 0
        private set
    /**
     * 获取当前登录用户的token。
     * @return 当前登录用户的token。
     */
    var token: String? = null
        private set
    /**
     * 获取当前登录用户的登录类型。
     * @return 当前登录用户登录类型。
     */
    var loginType = -1
        private set
    var BASE_URL = if (isDebug) "http://192.168.31.177:3000" else "http://api.quxianggif.com"
    const val GIF_MAX_SIZE = 20 * 1024 * 1024
    /**
     * 初始化接口。这里会进行应用程序的初始化操作，一定要在代码执行的最开始调用。
     *
     * @param c
     * Context参数，注意这里要传入的是Application的Context，千万不能传入Activity或者Service的Context。
     */
    fun initialize(c: Context?) {
        context = c
        handler = Handler(Looper.getMainLooper())
        refreshLoginState()
    }

    /**
     * 返回当前应用的包名。
     */
    val packageName: String
        get() = context!!.packageName

    /**
     * 注销用户登录。
     */
    fun logout() {
        clear(Const.Auth.USER_ID)
        clear(Const.Auth.TOKEN)
        clear(Const.Auth.LOGIN_TYPE)
        clear(Const.User.AVATAR)
        clear(Const.User.BG_IMAGE)
        clear(Const.User.DESCRIPTION)
        clear(Const.User.NICKNAME)
        clear(Const.Feed.MAIN_PAGER_POSITION)
        refreshLoginState()
    }

    /**
     * 刷新用户的登录状态。
     */
    fun refreshLoginState() {
        val u = read(Const.Auth.USER_ID, 0L)
        val t = read(Const.Auth.TOKEN, "")
        val lt = read(Const.Auth.LOGIN_TYPE, -1)
        isLogin = u > 0 && !TextUtils.isEmpty(t) && lt >= 0
        if (isLogin) {
            userId = u
            token = t
            loginType = lt
        }
    }
}