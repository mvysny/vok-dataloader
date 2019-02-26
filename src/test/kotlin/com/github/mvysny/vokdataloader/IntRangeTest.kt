package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import kotlin.test.expect

/**
 * @author mvy
 */
class IntRangeTest : DynaTest({
    test("emptyIntersection") {
        expect(IntRange.EMPTY) { (5..5).intersection(10..10) }
		expect(IntRange.EMPTY) { IntRange.EMPTY.intersection(10..10) }
		expect(IntRange.EMPTY) { IntRange.EMPTY.intersection(0..0) }
		expect(IntRange.EMPTY) { (0..0).intersection(IntRange.EMPTY) }
		expect(IntRange.EMPTY) { (0..0).intersection(1..1) }
		expect(IntRange.EMPTY) { (0..10).intersection(11..20) }
    }

    test("NonEmptyIntersection") {
        expect(0..0) { (0..0).intersection(0..0) }
        expect(0..0) { (0..0).intersection(0..10) }
        expect(10..10) { (0..10).intersection(10..12) }
        expect(2..5) { (0..10).intersection(2..5) }
        expect(2..10) { (0..10).intersection(2..25) }
        expect(2..10) { (2..25).intersection(0..10) }
    }

    test("contains") {
        expect(true) { (5..5).contains(5..5) }
        expect(true) { (5..5).contains(IntRange.EMPTY) }
        expect(false) { (5..5).contains(6..6) }
        expect(false) { (5..7).contains(6..8) }
        expect(true) { (5..7).contains(5..6) }
        expect(true) { (5..7).contains(6..7) }
        expect(true) { (5..7).contains(6..6) }
    }
})

