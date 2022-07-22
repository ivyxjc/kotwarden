@file:JvmName(name = "CommonUtil")

package com.ivyxjc.kotwarden.util

const val EMPTY_STRING = ""

fun isEmpty(str: String?): Boolean {
    return str == null || str.isEmpty()
}

fun isNotEmpty(str: String?): Boolean {
    return str != null && str.isNotEmpty()
}

fun isEmpty(data: Collection<*>?): Boolean {
    return data == null || data.isEmpty()
}
