package com.memos.app.utils

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {

    fun formatRelativeTime(timeString: String): String {
        return try {
            val instant = Instant.parse(timeString)
            val now = Instant.now()
            val minutes = ChronoUnit.MINUTES.between(instant, now)
            val hours = ChronoUnit.HOURS.between(instant, now)
            val days = ChronoUnit.DAYS.between(instant, now)

            when {
                minutes < 1  -> "Just now"
                minutes < 60 -> "${minutes}m ago"
                hours < 24   -> "${hours}h ago"
                days < 7     -> "${days}d ago"
                days < 30    -> "${days / 7}w ago"
                days < 365   -> "${days / 30}mo ago"
                else -> DateTimeFormatter
                    .ofPattern("MMM dd, yyyy")
                    .withZone(ZoneId.systemDefault())
                    .format(instant)
            }
        } catch (e: Exception) {
            timeString
        }
    }

    fun formatFullDate(timeString: String): String {
        return try {
            val instant = Instant.parse(timeString)
            DateTimeFormatter
                .ofPattern("MMM dd, yyyy  HH:mm")
                .withZone(ZoneId.systemDefault())
                .format(instant)
        } catch (e: Exception) {
            timeString
        }
    }
}
