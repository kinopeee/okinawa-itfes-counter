package jp.co.keyplanning.okinawacounter

import org.junit.Assert.assertEquals
import org.junit.Test

class AttendanceTest {
    @Test fun fiveIncrementsShowTenPercent() {
        val attendance = (1..5).fold(Attendance()) { state, _ -> state.increment() }
        assertEquals(5, attendance.visitors)
        assertEquals(10L, attendance.percent)
        assertEquals(0.1f, attendance.progress, 0.001f)
    }

    @Test fun capacityBoundaries() {
        assertEquals(CapacityLevel.NORMAL, Attendance(39).level)
        assertEquals(CapacityLevel.WARNING, Attendance(40).level)
        assertEquals(CapacityLevel.WARNING, Attendance(50).level)
        assertEquals(CapacityLevel.OVER, Attendance(51).level)
        assertEquals(102L, Attendance(51).percent)
        assertEquals(1f, Attendance(51).progress, 0f)
    }

    @Test fun resetClearsOverCapacity() {
        assertEquals(Attendance(), Attendance(51).reset())
    }

    @Test fun incrementDoesNotOverflow() {
        assertEquals(Int.MAX_VALUE, Attendance(Int.MAX_VALUE).increment().visitors)
        assertEquals(4294967294L, Attendance(Int.MAX_VALUE).percent)
    }
}
