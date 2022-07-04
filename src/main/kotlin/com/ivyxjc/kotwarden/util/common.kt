@file:JvmName(name = "CommonUtil")

package com.ivyxjc.kotwarden.util

const val EMPTY_STRING = ""

fun isEmpty(str: String?): Boolean {
    return str == null || str.isEmpty()
}
