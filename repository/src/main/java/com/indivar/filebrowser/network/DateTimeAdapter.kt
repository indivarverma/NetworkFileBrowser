package com.indivar.filebrowser.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

@Retention(AnnotationRetention.RUNTIME)
@JsonQualifier
internal annotation class DateString

class DateTimeAdapter : JsonAdapter<DateTime>() {

    @FromJson
    @DateString
    override fun fromJson(reader: JsonReader): DateTime? =
        try {
            FORMATTER.parseDateTime(reader.nextString())
        } catch (_: Exception) {
            null
        }

    @ToJson
    override fun toJson(writer: JsonWriter, @DateString value: DateTime?) {
        writer.value(FORMATTER.print(value))
    }

    companion object {
        val FORMATTER: DateTimeFormatter = ISODateTimeFormat.dateTimeParser()
    }
}