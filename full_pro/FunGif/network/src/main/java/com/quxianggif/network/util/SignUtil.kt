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
package com.quxianggif.network.util

import android.content.pm.PackageManager
import com.quxianggif.core.GifFun.context
import okhttp3.internal.and
import java.security.MessageDigest

object SignUtil {
    private val HEX_DIGITS =
        charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')

    private fun toHexString(b: ByteArray): String {
        val sb = StringBuilder(b.size * 2)
        for (aB in b) {
            sb.append(HEX_DIGITS[aB and 0xf0 ushr 4])
            sb.append(HEX_DIGITS[aB and 0x0f])
        }
        return sb.toString()
    }

    val appSignature: String
        get() = try {
            val info = context!!.packageManager.getPackageInfo(
                context!!.packageName,
                PackageManager.GET_SIGNATURES
            )
            val digest = MessageDigest.getInstance("MD5")
            val signatures = info.signatures
            if (signatures != null) {
                for (s in signatures) digest.update(s.toByteArray())
            }
            toHexString(digest.digest())
        } catch (e: Exception) {
            ""
        }
}