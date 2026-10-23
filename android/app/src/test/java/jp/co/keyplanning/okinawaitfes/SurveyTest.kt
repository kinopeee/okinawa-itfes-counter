package jp.co.keyplanning.okinawaitfes

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SurveyTest {
    @Test fun capacityBoundariesAndPercentage() {
        assertEquals(10L, percentage(5))
        assertEquals(Capacity.NORMAL, capacity(39))
        assertEquals(Capacity.WARNING, capacity(40))
        assertEquals(Capacity.WARNING, capacity(50))
        assertEquals(Capacity.OVER, capacity(51))
        assertEquals(102L, percentage(51))
        assertEquals(4294967294L, percentage(Int.MAX_VALUE))
    }

    @Test fun everyOptionIsPresentIncludingZeroCounts() {
        val responses = listOf(Response.create(Answer(5, "sns", "20s")), Response.create(Answer(3, "other", "no_answer")))
        assertEquals(listOf(0, 0, 1, 0, 1), counts(responses, Questions.satisfaction) { it.satisfaction.toString() })
        assertEquals(listOf(1, 0, 0, 0, 1), counts(responses, Questions.discovery) { it.discovery })
        assertEquals(listOf(0, 1, 0, 0, 0, 0, 1), counts(responses, Questions.age) { it.ageGroup })
        assertEquals(List(7) { 0 }, counts(emptyList(), Questions.age) { it.ageGroup })
    }

    @Test fun rejectsInvalidValuesAndMetadata() {
        assertThrows(IllegalArgumentException::class.java) { Answer(0, "sns", "20s") }
        assertThrows(IllegalArgumentException::class.java) { Answer(6, "sns", "20s") }
        assertThrows(IllegalArgumentException::class.java) { Answer(5, "unknown", "20s") }
        assertThrows(IllegalArgumentException::class.java) { Answer(5, "sns", "unknown") }
        assertThrows(IllegalArgumentException::class.java) { Response("1-1-1-1-1", "2026-10-23T05:00:00Z", Answer(5, "sns", "20s")) }
        assertThrows(IllegalArgumentException::class.java) {
            Response("12345678-1234-1234-1234-123456789abc", "2026-10-23T14:00:00+09:00", Answer(5, "sns", "20s"))
        }
    }
}
