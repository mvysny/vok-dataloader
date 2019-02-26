package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import kotlin.test.expect

class UtilsTest : DynaTest({
    group("length") {
        test("IntRange") {
            expect(0) { IntRange.EMPTY.length }
            expect(0) { IntRange(0, -10).length }
            expect(0) { IntRange(0, Int.MIN_VALUE).length }
            expect(Int.MAX_VALUE) { IntRange(0, Int.MAX_VALUE).length }
            expect(Int.MAX_VALUE) { IntRange(0, Int.MAX_VALUE - 1).length }
            expect(Int.MAX_VALUE - 1) { IntRange(0, (Int.MAX_VALUE - 2)).length }
            expect(11) { IntRange(0, 10).length }
            expect(11) { IntRange(10, 20).length }
        }
        test("LongRange") {
            expect(0) { LongRange.EMPTY.length }
            expect(0) { LongRange(0, -10).length }
            expect(0) { LongRange(0, Long.MIN_VALUE).length }
            expect(Long.MAX_VALUE) { LongRange(0, Long.MAX_VALUE).length }
            expect(Long.MAX_VALUE) { LongRange(0, Long.MAX_VALUE - 1).length }
            expect(Long.MAX_VALUE - 1) { LongRange(0, Long.MAX_VALUE - 2).length }
            expect(11) { LongRange(0, 10).length }
            expect(11) { LongRange(10, 20).length }
        }
    }

    test("subList") {
        expectList(1, 2, 3) { listOf(1, 2, 3).subList(0..2) }
        expectList(2, 3) { listOf(1, 2, 3).subList(1..2) }
        expectList(1, 2) { listOf(1, 2, 3).subList(0..1) }
        expectList() { listOf(1, 2, 3).subList(IntRange.EMPTY) }
        expectList() { listOf<Int>().subList(IntRange.EMPTY) }
    }
})
