package uz.warcom.contest.bot.util

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

object DateHelper {
    private val defaultFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")

    fun format (format: String, temporalAccessor: TemporalAccessor): String {
        return DateTimeFormatter.ofPattern(format).format(temporalAccessor)
    }

    fun format (temporalAccessor: TemporalAccessor): String {
        return defaultFormatter.format(temporalAccessor)
    }
}