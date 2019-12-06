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

import java.lang.reflect.InvocationTargetException

/**
 * This provides a send method to allow calling method in dynamic way.
 *
 * @author guolin
 * @since 17/3/25
 */
object Reflection {
    /**
     * This method use java reflect API to execute method dynamically. Most
     * importantly, it could access the methods with private modifier to break
     * encapsulation.
     *
     * @param object         The object to invoke method.
     * @param methodName     The method name to invoke.
     * @param parameters     The parameters.
     * @param objectClass    Use objectClass to find method to invoke.
     * @param parameterTypes The parameter types.
     * @return Returns the result of dynamically invoking method.
     */
    @Throws(
        SecurityException::class,
        IllegalArgumentException::class,
        IllegalAccessException::class,
        InvocationTargetException::class,
        NoSuchMethodException::class
    )
    fun send(
        `object`: Any?, methodName: String?, parameters: Array<Any?>?, objectClass: Class<*>,
        parameterTypes: Array<Class<*>?>?
    ): Any {
        var parameters = parameters
        var parameterTypes = parameterTypes
        if (parameters == null) {
            parameters = arrayOf()
        }
        if (parameterTypes == null) {
            parameterTypes = arrayOf()
        }
        val method = objectClass.getDeclaredMethod(methodName!!, *parameterTypes)
        method.isAccessible = true
        return method.invoke(`object`, *parameters)
    }

    /**
     * This method use java reflect API to set field value dynamically. Most
     * importantly, it could access fields with private modifier to break
     * encapsulation.
     *
     * @param object      The object to access.
     * @param fieldName   The field name to access.
     * @param value       Assign this value to field.
     * @param objectClass The class of object.
     */
    @Throws(
        SecurityException::class,
        IllegalArgumentException::class,
        IllegalAccessException::class,
        NoSuchFieldException::class
    )
    fun setField(`object`: Any?, fieldName: String?, value: Any?, objectClass: Class<*>) {
        val objectField = objectClass.getDeclaredField(fieldName!!)
        objectField.isAccessible = true
        objectField[`object`] = value
    }

    /**
     * This method use java reflect API to get field value dynamically. Most
     * importantly, it could access fields with private modifier to break
     * encapsulation.
     *
     * @param object      The object to access.
     * @param fieldName   The field name to access.
     * @param objectClass The class of object.
     */
    @Throws(
        IllegalArgumentException::class,
        IllegalAccessException::class,
        NoSuchFieldException::class
    )
    fun getField(`object`: Any?, fieldName: String?, objectClass: Class<*>): Any {
        val objectField = objectClass.getDeclaredField(fieldName!!)
        objectField.isAccessible = true
        return objectField[`object`]
    }
}