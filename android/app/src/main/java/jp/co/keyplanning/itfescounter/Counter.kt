package jp.co.keyplanning.itfescounter

internal const val CAPACITY = 50

internal fun capacityPercentage(visitors: Int): Long = visitors.toLong() * 100 / CAPACITY

internal fun isOverCapacity(visitors: Int): Boolean = visitors > CAPACITY
