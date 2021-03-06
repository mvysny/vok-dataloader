package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import kotlin.test.expect

class EmptyDataLoaderTest : DynaTest({
    test("count") {
        val dl = EmptyDataLoader<Person>()
        expect(0) { dl.getCount(null) }
        expect(0) { dl.getCount(buildFilter { Person::name eq "John" }) }
    }

    test("fetch") {
        val dl = EmptyDataLoader<Person>()
        expectList() { dl.fetch() }
        expectList() { dl.fetch(range = 5000L..6000L) }
        expectList() { dl.fetch(range = LongRange.EMPTY) }
        expectList() { dl.fetch(buildFilter { Person::name eq "John" }) }
        expectList() { dl.fetch(sortBy = listOf(Person::name.asc)) }
        expectList() { dl.fetch(buildFilter { Person::name eq "John" }, range = 5000L..6000L) }
        expectList() { dl.fetch(sortBy = listOf(Person::name.asc), range = 5000L..6000L) }
        expectList() { dl.fetch(buildFilter { Person::name eq "John" }, sortBy = listOf(String::length.asc), range = 5000L..6000L) }
    }
})
