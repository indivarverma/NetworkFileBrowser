package com.indivar.filebrowser.network

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

class JodaDateTimeAdapter : TypeAdapter<DateTime>() {
    override fun write(writer: JsonWriter?, value: DateTime?) {

        writer?.value(
            FORMATTER.print(value)
        )
    }

    override fun read(reader: JsonReader?): DateTime =
        DateTimeAdapter.FORMATTER.parseDateTime(reader?.nextString())


    companion object {
        val FORMATTER: DateTimeFormatter = ISODateTimeFormat.dateTime()
    }
}