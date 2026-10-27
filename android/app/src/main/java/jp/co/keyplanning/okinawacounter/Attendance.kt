package jp.co.keyplanning.okinawacounter

internal const val CAPACITY = 50

internal enum class CapacityLevel { NORMAL, WARNING, OVER }

internal data class Attendance(val visitors: Int = 0) {
    val percent: Long get() = visitors.toLong() * 100 / CAPACITY
    val progress: Float get() = (visitors.toFloat() / CAPACITY).coerceIn(0f, 1f)
    val level: CapacityLevel
        get() = when {
            visitors > CAPACITY -> CapacityLevel.OVER
            visitors >= 40 -> CapacityLevel.WARNING
            else -> CapacityLevel.NORMAL
        }

    fun increment(): Attendance = copy(visitors = (visitors.toLong() + 1).coerceAtMost(Int.MAX_VALUE.toLong()).toInt())
    fun reset(): Attendance = Attendance()
}
