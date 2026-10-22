package jp.co.keyplanning.itfescounter

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CounterTest {
    @Test
    fun fiveVisitorsUseTenPercentOfCapacity() {
        assertEquals(0L, capacityPercentage(0))
        assertEquals(10L, capacityPercentage(5))
    }

    @Test
    fun warningStartsOnlyAboveFiftyVisitors() {
        assertEquals(100L, capacityPercentage(50))
        assertFalse(isOverCapacity(50))
        assertEquals(102L, capacityPercentage(51))
        assertTrue(isOverCapacity(51))
    }

    @Test
    fun percentageDoesNotOverflowForLargeCounts() {
        assertEquals(4_294_967_294L, capacityPercentage(Int.MAX_VALUE))
    }
}
