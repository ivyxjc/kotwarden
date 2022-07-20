@file:JvmName(name = "DynamodbUtil")

package com.ivyxjc.kotwarden.util

import software.amazon.awssdk.core.pagination.sync.SdkIterable
import software.amazon.awssdk.enhanced.dynamodb.MappedTableResource
import software.amazon.awssdk.enhanced.dynamodb.model.BatchGetResultPageIterable
import software.amazon.awssdk.enhanced.dynamodb.model.Page

fun <T> convert(iter: SdkIterable<Page<T>>): List<T> {
    val list = mutableListOf<T>()
    for (p in iter) {
        list.addAll(p.items())
    }
    return list
}

fun <T> convert(iter: BatchGetResultPageIterable, table: MappedTableResource<T>): List<T> {
    val list = mutableListOf<T>()
    for (p in iter) {
        list.addAll(p.resultsForTable(table))
    }
    return list
}