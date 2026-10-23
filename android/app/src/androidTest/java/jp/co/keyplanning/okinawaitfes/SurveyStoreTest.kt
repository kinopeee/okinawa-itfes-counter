package jp.co.keyplanning.okinawaitfes

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.IOException
import java.util.UUID
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SurveyStoreTest {
    private val answer = Answer(5, "sns", "20s")
    private val valid get() = SurveyJson.encode(listOf(Response.create(answer)))

    private class MemoryFile(var content: String? = null) : SurveyFile {
        var failWrite = false
        var failRead = false
        override fun read(): String? {
            if (failRead) throw IOException("read failure")
            return content
        }
        override fun replace(contents: String) {
            if (failWrite) throw IOException("write failure")
            content = contents
        }
    }

    @Test fun firstRunRoundTripAndRestart() {
        val file = MemoryFile()
        val store = SurveyStore(file)
        assertTrue(store.load().isEmpty())
        val first = store.save(answer)
        val second = store.save(Answer(3, "other", "no_answer"))
        assertEquals(1, first.size)
        assertEquals(2, second.size)
        assertNotEquals(second[0].id, second[1].id)
        assertEquals(second, SurveyStore(file).load())
    }

    @Test fun invalidDataCannotBeOverwrittenUntilSuccessfulReload() {
        val json = valid
        val cases = listOf(
            "", "{broken", "{}", "[]", json + " trailing",
            json.replace("\"schemaVersion\":1", "\"schemaVersion\":2"),
            json.replace("\"schemaVersion\":1", "\"schemaVersion\":\"1\""),
            json.replace("\"satisfaction\":5", "\"satisfaction\":5.0"),
            json.replace("\"satisfaction\":5", "\"satisfaction\":6"),
            json.replace("\"sns\"", "\"bad\""), json.replace("\"20s\"", "null"),
            json.replace("\"schemaVersion\":1", "\"schemaVersion\":1,\"schemaVersion\":1"),
            json.replace("\"satisfaction\":5", "\"satisfaction\":5,\"satisfaction\":4"),
            "{\"schemaVersion\":1,\"responses\":null}",
            json.replace("\"ageGroup\":\"20s\"", "\"extra\":true"),
        )
        cases.forEach { broken ->
            val file = MemoryFile(broken)
            val store = SurveyStore(file)
            assertThrows(SurveyReadException::class.java) { store.load() }
            assertThrows(IllegalStateException::class.java) { store.save(answer) }
            assertEquals(broken, file.content)
            file.content = json
            assertThrows(IllegalStateException::class.java) { store.save(answer) }
            store.load()
            assertEquals(2, store.save(answer).size)
        }
    }

    @Test fun readFailureAndExternalCorruptionBlockSaving() {
        val file = MemoryFile(valid)
        val store = SurveyStore(file)
        store.load()
        file.failRead = true
        assertThrows(SurveyReadException::class.java) { store.save(answer) }
        file.failRead = false
        store.load()
        file.content = "broken"
        assertThrows(SurveyReadException::class.java) { store.save(answer) }
        assertEquals("broken", file.content)
    }

    @Test fun writeFailurePreservesExistingDataAndAllowsRetry() {
        val file = MemoryFile(valid)
        val store = SurveyStore(file)
        val original = store.load()
        val bytes = file.content
        file.failWrite = true
        assertThrows(IOException::class.java) { store.save(answer) }
        assertEquals(bytes, file.content)
        assertEquals(original, store.load())
        file.failWrite = false
        assertEquals(2, store.save(answer).size)
    }

    @Test fun actualInternalFileRoundTripAndFailedRenamePreservesTarget() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.cacheDir, "store-test-${UUID.randomUUID()}")
        assertTrue(directory.mkdir())
        try {
            val file = InternalSurveyFile(directory)
            val store = SurveyStore(file)
            assertTrue(store.load().isEmpty())
            val saved = store.save(answer)
            assertEquals(saved, SurveyStore(InternalSurveyFile(directory)).load())
            val target = File(directory, "survey-responses.json")
            assertTrue(target.delete())
            assertTrue(target.mkdir())
            val sentinel = File(target, "sentinel").apply { writeText("existing") }
            assertThrows(Exception::class.java) { file.replace(valid) }
            assertEquals("existing", sentinel.readText())
            assertEquals(listOf("survey-responses.json"), directory.list()!!.toList())
        } finally {
            directory.deleteRecursively()
        }
    }

    @Test fun missingParentDirectoryIsNotTreatedAsEmpty() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.cacheDir, "missing-${UUID.randomUUID()}")
        assertThrows(SurveyReadException::class.java) { SurveyStore(InternalSurveyFile(directory)).load() }
    }

    @Test fun rejectsInvalidUtf8AndDuplicateResponseIds() {
        val response = Response.create(answer)
        assertThrows(Exception::class.java) { SurveyJson.decode(SurveyJson.encode(listOf(response, response))) }
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val directory = File(context.cacheDir, "utf8-test-${UUID.randomUUID()}")
        assertTrue(directory.mkdir())
        try {
            val target = File(directory, "survey-responses.json")
            target.writeBytes(byteArrayOf(0xC3.toByte(), 0x28))
            val original = target.readBytes()
            val store = SurveyStore(InternalSurveyFile(directory))
            assertThrows(SurveyReadException::class.java) { store.load() }
            assertThrows(IllegalStateException::class.java) { store.save(answer) }
            assertArrayEquals(original, target.readBytes())
        } finally {
            directory.deleteRecursively()
        }
    }
}
