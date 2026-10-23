package jp.co.keyplanning.okinawaitfes

import android.system.Os
import android.util.JsonReader
import android.util.JsonToken
import android.util.JsonWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.StringReader
import java.io.StringWriter
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.file.Files
import java.nio.file.NoSuchFileException

class SurveyReadException(cause: Exception) : IOException("回答データを読み込めません", cause)

interface SurveyFile {
    fun read(): String?
    fun replace(contents: String)
}

class InternalSurveyFile(directory: File) : SurveyFile {
    private val target = File(directory, "survey-responses.json")

    override fun read(): String? {
        val bytes = try {
            Files.readAllBytes(target.toPath())
        } catch (error: NoSuchFileException) {
            if (!target.parentFile!!.isDirectory) throw IOException("保存先を読み込めません", error)
            return null
        }
        val decoder = Charsets.UTF_8.newDecoder()
            .onMalformedInput(CodingErrorAction.REPORT)
            .onUnmappableCharacter(CodingErrorAction.REPORT)
        return decoder.decode(ByteBuffer.wrap(bytes)).toString()
    }

    override fun replace(contents: String) {
        val temporary = File.createTempFile("survey-", ".tmp", target.parentFile)
        try {
            FileOutputStream(temporary).use { output ->
                output.write(contents.toByteArray(Charsets.UTF_8))
                output.fd.sync()
            }
            // Same-directory POSIX rename atomically replaces the file and reports failures.
            Os.rename(temporary.absolutePath, target.absolutePath)
        } finally {
            temporary.delete()
        }
    }
}

class SurveyStore(private val file: SurveyFile) {
    private var readable = false

    @Synchronized
    fun load(): List<Response> {
        readable = false
        try {
            val text = file.read()
            val responses = if (text == null) emptyList() else SurveyJson.decode(text)
            readable = true
            return responses
        } catch (error: Exception) {
            throw SurveyReadException(error)
        }
    }

    @Synchronized
    fun save(answer: Answer): List<Response> {
        check(readable) { "回答データの読み直しが必要です" }
        val next = load() + Response.create(answer)
        file.replace(SurveyJson.encode(next))
        return next
    }
}

object SurveyJson {
    fun encode(responses: List<Response>): String {
        val result = StringWriter()
        JsonWriter(result).use { writer ->
            writer.beginObject().name("schemaVersion").value(1L).name("responses").beginArray()
            responses.forEach { response ->
                writer.beginObject()
                    .name("id").value(response.id)
                    .name("submittedAt").value(response.submittedAt)
                    .name("satisfaction").value(response.answer.satisfaction.toLong())
                    .name("discovery").value(response.answer.discovery)
                    .name("ageGroup").value(response.answer.ageGroup)
                    .endObject()
            }
            writer.endArray().endObject()
        }
        return result.toString()
    }

    fun decode(text: String): List<Response> = JsonReader(StringReader(text)).use { reader ->
        reader.isLenient = false
        var version: Int? = null
        var responses: List<Response>? = null
        val keys = mutableSetOf<String>()
        reader.beginObject()
        while (reader.hasNext()) {
            val key = reader.nextName()
            require(keys.add(key)) { "フィールドが重複しています" }
            when (key) {
                "schemaVersion" -> version = reader.strictInt()
                "responses" -> {
                    val entries = mutableListOf<Response>()
                    val ids = mutableSetOf<String>()
                    reader.beginArray()
                    while (reader.hasNext()) {
                        val response = reader.response()
                        require(ids.add(response.id.lowercase())) { "回答IDが重複しています" }
                        entries.add(response)
                    }
                    reader.endArray()
                    responses = entries
                }
                else -> error("未対応のフィールドです")
            }
        }
        reader.endObject()
        require(version == 1) { "未対応のデータバージョンです" }
        require(reader.peek() == JsonToken.END_DOCUMENT) { "余分なデータがあります" }
        requireNotNull(responses)
    }

    private fun JsonReader.strictInt(): Int {
        require(peek() == JsonToken.NUMBER)
        val value = nextString()
        require(value.matches(Regex("-?(0|[1-9][0-9]*)")))
        return value.toInt()
    }

    private fun JsonReader.strictString(): String {
        require(peek() == JsonToken.STRING)
        return nextString()
    }

    private fun JsonReader.response(): Response {
        var id: String? = null
        var submittedAt: String? = null
        var satisfaction: Int? = null
        var discovery: String? = null
        var age: String? = null
        val keys = mutableSetOf<String>()
        beginObject()
        while (hasNext()) {
            val key = nextName()
            require(keys.add(key))
            when (key) {
                "id" -> id = strictString()
                "submittedAt" -> submittedAt = strictString()
                "satisfaction" -> satisfaction = strictInt()
                "discovery" -> discovery = strictString()
                "ageGroup" -> age = strictString()
                else -> error("未対応の回答フィールドです")
            }
        }
        endObject()
        return Response(
            requireNotNull(id), requireNotNull(submittedAt),
            Answer(requireNotNull(satisfaction), requireNotNull(discovery), requireNotNull(age)),
        )
    }
}
