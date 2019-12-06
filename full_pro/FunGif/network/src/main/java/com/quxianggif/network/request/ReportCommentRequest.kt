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

package com.quxianggif.network.request

import com.quxianggif.core.GifFun
import com.quxianggif.network.model.Callback
import com.quxianggif.network.model.ReportComment
import com.quxianggif.network.util.NetworkConst
import okhttp3.Headers
import java.util.*

/**
 * 举报评论请求。对应服务器接口：/report/comment
 *
 * @author guolin
 * @since 18/8/29
 */
class ReportCommentRequest : Request() {

    private var comment: Long = 0

    private var reason = 0

    private var desp = ""

    fun comment(comment: Long): ReportCommentRequest {
        this.comment = comment
        return this
    }

    fun reason(reason: Int): ReportCommentRequest {
        this.reason = reason
        return this
    }

    fun desp(desp: String): ReportCommentRequest {
        this.desp = desp
        return this
    }

    override fun url(): String {
        return URL
    }

    override fun method(): Int {
        return Request.POST
    }

    override fun listen(callback: Callback?) {
        setListener(callback)
        inFlight(ReportComment::class.java)
    }

    override fun params(): Map<String, String>? {
        val params = HashMap<String, String>()
        if (buildAuthParams(params)) {
            params[NetworkConst.COMMENT] = comment.toString()
            params[NetworkConst.REASON] = reason.toString()
            if (desp.isNotBlank()) {
                params[NetworkConst.DESCRIPTION] = desp
            }
            return params
        }
        return super.params()
    }

    override fun headers(builder: Headers.Builder): Headers.Builder {
        buildAuthHeaders(builder, NetworkConst.COMMENT, NetworkConst.UID, NetworkConst.REASON)
        return super.headers(builder)
    }

    companion object {

        private val URL = GifFun.BASE_URL + "/report/comment"
    }
}
