package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import kotlin.test.expect

/**
 * @author mvy
 */
class LongRangeTest : DynaTest({
    test("emptyIntersection") {
        expect(LongRange.EMPTY) { (5L..5L).intersection(10L..10L) }
        expect(LongRange.EMPTY) { LongRange.EMPTY.intersection(10L..10L) }
        expect(LongRange.EMPTY) { LongRange.EMPTY.intersection(0L..0L) }
        expect(LongRange.EMPTY) { (0L..0L).intersection(LongRange.EMPTY) }
        expect(LongRange.EMPTY) { (0L..0L).intersection(1L..1L) }
        expect(LongRange.EMPTY) { (0L..10L).intersection(11L..20L) }
    }

    test("NonEmptyIntersection") {
        expect(0L..0L) { (0L..0).intersection(0L..0) }
        expect(0L..0L) { (0L..0).intersection(0L..10) }
        expect(10L..10L) { (0L..10).intersection(10L..12) }
        expect(2L..5L) { (0L..10).intersection(2L..5) }
        expect(2L..10L) { (0L..10).intersection(2L..25) }
        expect(2L..10L) { (2L..25).intersection(0L..10) }
    }

    test("contains") {
        expect(true) { (5L..5).contains(5L..5) }
        expect(true) { (5L..5).contains(LongRange.EMPTY) }
        expect(false) { (5L..5).contains(6L..6) }
        expect(false) { (5L..7).contains(6L..8) }
        expect(true) { (5L..7).contains(5L..6) }
        expect(true) { (5L..7).contains(6L..7) }
        expect(true) { (5L..7).contains(6L..6) }
    }
})
