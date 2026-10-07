package com.sharechat.feed.config

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Instant

@Configuration
class GsonConfig {

    @Bean
    fun gson(): Gson = GsonBuilder()
        .registerTypeAdapter(Instant::class.java, InstantAdapter())
        .create()
}

/** Writes Instant as ISO-8601 (e.g. 2026-10-06T08:00:00Z), as required by the contract. */
private class InstantAdapter : TypeAdapter<Instant>() {
    override fun write(out: JsonWriter, value: Instant?) {
        if (value == null) out.nullValue() else out.value(value.toString())
    }

    override fun read(reader: JsonReader): Instant = Instant.parse(reader.nextString())
}
