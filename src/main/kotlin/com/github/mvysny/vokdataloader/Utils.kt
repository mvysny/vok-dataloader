package com.github.mvysny.vokdataloader

import java.beans.Introspector
import java.lang.reflect.Method

/**
 * Returns a getter method for given [propertyName] for this class. Fails if there is no such property, or if the
 * property is write-only (it doesn't have a getter).
 */
fun Class<*>.getGetter(propertyName: String): Method {
    val propertyDescriptor = Introspector.getBeanInfo(this).propertyDescriptors.firstOrNull { it.name == propertyName }
            ?: throw IllegalStateException("Bean $this has no property $propertyName")
    return propertyDescriptor.readMethod
            ?: throw IllegalStateException("Bean $this has no readMethod for property $propertyDescriptor")
}

val IntRange.length: Int get() {
    if (isEmpty()) return 0
    val len = endInclusive - start + 1
    return if (len < 0) Int.MAX_VALUE else len
}

val LongRange.length: Long get() {
    if (isEmpty()) return 0
    val len = endInclusive - start + 1
    return if (len < 0) Long.MAX_VALUE else len
}

/**
 * Returns a range that is an intersection of this range and [other].
 */
fun IntRange.intersection(other: IntRange): IntRange {
    if (this.start > other.endInclusive || this.endInclusive < other.start) {
        return IntRange.EMPTY
    }
    if (contains(other)) {
        return other
    }
    if (other.contains(this)) {
        return this
    }
    val s = this.start.coerceAtLeast(other.start)
    val e = this.endInclusive.coerceAtMost(other.endInclusive)
    assert(s <= e)
    return s..e
}

/**
 * Checks whether this range fully contains the [other] range.
 */
operator fun <T: Comparable<T>> ClosedRange<T>.contains(other: ClosedRange<T>) =
        other.isEmpty() || (start <= other.start && endInclusive >= other.endInclusive)

/**
 * Returns a range that is an intersection of this range and [other].
 */
fun LongRange.intersection(other: LongRange): LongRange {
    if (this.start > other.endInclusive || this.endInclusive < other.start) {
        return LongRange.EMPTY
    }
    if (contains(other)) {
        return other
    }
    if (other.contains(this)) {
        return this
    }
    val s = this.start.coerceAtLeast(other.start)
    val e = this.endInclusive.coerceAtMost(other.endInclusive)
    assert(s <= e)
    return s..e
}

val ClosedRange<Int>.longRange: LongRange get() = start.toLong()..endInclusive.toLong()

val ClosedRange<Long>.intRange: IntRange get() = start.coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()..
        endInclusive.coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()

/**
 * Returns a view of the portion of this list between the specified [indexRange].
 * The returned list is backed by this list, so non-structural changes in the returned list are reflected in this list, and vice-versa.
 *
 * Structural changes in the base list make the behavior of the view undefined.
 */
fun <T> List<T>.subList(indexRange: IntRange): List<T> {
    if (indexRange.isEmpty()) {
        val index = indexRange.start.coerceIn(0..size)
        return subList(index, index)
    }
    return subList(indexRange.start, if (indexRange.endInclusive == Int.MAX_VALUE) Int.MAX_VALUE else indexRange.endInclusive + 1)
}
