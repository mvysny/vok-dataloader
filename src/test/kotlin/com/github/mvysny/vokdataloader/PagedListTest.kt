package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import kotlin.test.expect

class PagedListTest : DynaTest({
    test("empty") {
        expectList() { PagedList(EmptyDataLoader<Int>(), 5).iterator().asSequence().toList() }
        expect(0) { PagedList(EmptyDataLoader<Int>(), 5).size }
        expect(true) { PagedList(EmptyDataLoader<Int>(), 5).isEmpty() }
    }

    test("simple iteration") {
        val list = (0..10).toList()
        expect(list) { PagedList(list.dataLoader(), 3).toList() }
    }

    test("long iteration") {
        val list = (0..1000).toList()
        expect(list) { PagedList(list.dataLoader(), 7).toList() }
    }

    test("keeps at most three pages") {
        val list = PagedList((0..1000).toList().dataLoader(), 70)
        for (i in 0..1000) {
            expect(i) { list[i] }
            expect(true, "${list.cache}") { list.cache.size <= 3}
        }
    }
})