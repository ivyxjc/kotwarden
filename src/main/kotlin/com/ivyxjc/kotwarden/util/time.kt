package com.ivyxjc.kotwarden.util

import org.slf4j.LoggerFactory
import java.text.MessageFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

private val log = LoggerFactory.getLogger("TimeUtils")
val pattern = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss.SSSSSS").withZone(ZoneOffset.UTC)
val patterns = listOf(pattern)
fun parse(timeStr: String?): OffsetDateTime? {
    if (timeStr?.isEmpty() == true) {
        return null
    }
    var result: OffsetDateTime? = null
    for (fmt in patterns) {
        try {
            result = OffsetDateTime.parse(timeStr, fmt)
            break
        } catch (e: Exception) {
            log.error("Not success to parse string {} to offset datetime", timeStr)
        }
    }
    if (result == null) {
        log.error("Fail to parse string {} to offset datetime", timeStr)
        throw DateTimeParseException(MessageFormat.format("Fail to parse {0} to offset datetime", timeStr), timeStr, 0)
    }
    return result.withOffsetSameInstant(ZoneOffset.UTC)
}

fun format(dateTime: OffsetDateTime?): String {
    if (dateTime == null) {
        return EMPTY_STRING
    }
    return dateTime.format(pattern)
}