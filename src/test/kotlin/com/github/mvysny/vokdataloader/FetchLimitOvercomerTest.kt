package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import kotlin.test.expect

class FetchLimitOvercomerTest : DynaTest({
    test("empty delegate") {
        expectList() { emptydl.overcomeFetchLimit(1).fetch(range = 0L..9) }
        expectList() { emptydl.overcomeFetchLimit(5).fetch(range = 0L..9) }
        expectList() { emptydl.overcomeFetchLimit(9).fetch(range = 0L..9) }
        expectList() { emptydl.overcomeFetchLimit(10).fetch(range = 0L..9) }
        expectList() { emptydl.overcomeFetchLimit(20).fetch(range = 0L..9) }
        expectList() { emptydl.overcomeFetchLimit(100).fetch(range = 0L..9) }
        expectList() { emptydl.overcomeFetchLimit(1).fetch(range = 10L..19) }
        expectList() { emptydl.overcomeFetchLimit(5).fetch(range = 10L..19) }
        expectList() { emptydl.overcomeFetchLimit(9).fetch(range = 10L..19) }
        expectList() { emptydl.overcomeFetchLimit(10).fetch(range = 10L..19) }
        expectList() { emptydl.overcomeFetchLimit(20).fetch(range = 10L..19) }
        expectList() { emptydl.overcomeFetchLimit(100).fetch(range = 10L..19) }
    }

    test("delegate with 1 item") {
        val dl = listOf(25).dl
        expectList(25) { dl.overcomeFetchLimit(1).fetch(range = 0L..9) }
        expectList(25) { dl.overcomeFetchLimit(5).fetch(range = 0L..9) }
        expectList(25) { dl.overcomeFetchLimit(9).fetch(range = 0L..9) }
        expectList(25) { dl.overcomeFetchLimit(10).fetch(range = 0L..9) }
        expectList(25) { dl.overcomeFetchLimit(20).fetch(range = 0L..9) }
        expectList(25) { dl.overcomeFetchLimit(100).fetch(range = 0L..9) }
        expectList() { dl.overcomeFetchLimit(1).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(5).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(9).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(10).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(20).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(100).fetch(range = 10L..19) }
    }

    test("delegate with 5 items") {
        val dl = listOf(0, 1, 2, 3, 4).dl
        expectList(0, 1, 2, 3, 4) { dl.overcomeFetchLimit(1).fetch(range = 0L..9) }
        expectList(0, 1, 2, 3, 4) { dl.overcomeFetchLimit(5).fetch(range = 0L..9) }
        expectList(0, 1, 2, 3, 4) { dl.overcomeFetchLimit(9).fetch(range = 0L..9) }
        expectList(0, 1, 2, 3, 4) { dl.overcomeFetchLimit(10).fetch(range = 0L..9) }
        expectList(0, 1, 2, 3, 4) { dl.overcomeFetchLimit(20).fetch(range = 0L..9) }
        expectList(0, 1, 2, 3, 4) { dl.overcomeFetchLimit(100).fetch(range = 0L..9) }
        expectList() { dl.overcomeFetchLimit(1).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(5).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(9).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(10).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(20).fetch(range = 10L..19) }
        expectList() { dl.overcomeFetchLimit(100).fetch(range = 10L..19) }
    }

    test("delegate with 20 items") {
        val dl = (0..19).toList().dl
        expect((0..9).toList()) { dl.overcomeFetchLimit(1).fetch(range = 0L..9) }
        expect((0..9).toList()) { dl.overcomeFetchLimit(5).fetch(range = 0L..9) }
        expect((0..9).toList()) { dl.overcomeFetchLimit(9).fetch(range = 0L..9) }
        expect((0..9).toList()) { dl.overcomeFetchLimit(10).fetch(range = 0L..9) }
        expect((0..9).toList()) { dl.overcomeFetchLimit(20).fetch(range = 0L..9) }
        expect((0..9).toList()) { dl.overcomeFetchLimit(100).fetch(range = 0L..9) }
        expect((10..19).toList()) { dl.overcomeFetchLimit(1).fetch(range = 10L..19) }
        expect((10..19).toList()) { dl.overcomeFetchLimit(5).fetch(range = 10L..19) }
        expect((10..19).toList()) { dl.overcomeFetchLimit(9).fetch(range = 10L..19) }
        expect((10..19).toList()) { dl.overcomeFetchLimit(10).fetch(range = 10L..19) }
        expect((10..19).toList()) { dl.overcomeFetchLimit(20).fetch(range = 10L..19) }
        expect((10..19).toList()) { dl.overcomeFetchLimit(100).fetch(range = 10L..19) }
    }
})

private val List<Int>.dl: DataLoader<Int> get() = dataLoader()
private val emptydl = EmptyDataLoader<Int>()
