package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import com.github.mvysny.dynatest.expectList
import kotlin.test.expect

class ListDataLoaderTest : DynaTest({
    group("empty list") {
        test("count") {
            val dl = ListDataLoader(Person::class.java, listOf())
            expect(0) { dl.getCount(null) }
            expect(0) { dl.getCount(buildFilter { Person::name eq "John" }) }
        }

        test("fetch") {
            val dl = ListDataLoader(Person::class.java, listOf())
            expectList() { dl.fetch() }
            expectList() { dl.fetch(range = 5000L..4999L) }
            expectList() { dl.fetch(range = 5000L..6000L) }
            expectList() { dl.fetch(buildFilter { Person::name eq "John" }) }
            expectList() { dl.fetch(sortBy = listOf(Person::name.asc)) }
            expectList() { dl.fetch(buildFilter { Person::name eq "John" }, range = 5000L..6000L) }
            expectList() { dl.fetch(sortBy = listOf(Person::name.asc), range = 5000L..6000L) }
            expectList() { dl.fetch(buildFilter { Person::name eq "John" }, sortBy = listOf(Person::name.asc), range = 5000L..6000L) }
        }
    }

    group("10 items") {
        test("count") {
            val dl = ListDataLoader(Person::class.java, (0..9).map { Person("name $it") })
            expect(10) { dl.getCount(null) }
            expect(0) { dl.getCount(buildFilter { Person::name eq "John" }) }
            expect(1) { dl.getCount(buildFilter { Person::name eq "name 5" }) }
            expect(10) { dl.getCount(buildFilter { Person::name ilike "Name" }) }
        }

        test("fetch") {
            val list = (0..9).map { Person("name $it") }
            val dl = ListDataLoader(Person::class.java, list)
            expect(list) { dl.fetch() }
            expectList() { dl.fetch(range = 5000L..6000L) }
            expectList() { dl.fetch(range = 5000L..4999L) }
            expectList(list[5]) { dl.fetch(buildFilter { Person::name eq "name 5" }) }
            expect(list) { dl.fetch(sortBy = listOf(Person::name.asc)) }
            expect(list.reversed()) { dl.fetch(sortBy = listOf(Person::name.desc)) }
            expectList() { dl.fetch(buildFilter { Person::name eq "name 5" }, range = 5000L..6000L) }
            expectList(list[5]) { dl.fetch(buildFilter { Person::name eq "name 5" }, range = 0L..3L) }
            expectList() { dl.fetch(sortBy = listOf(Person::name.asc), range = 5000L..6000L) }
            expect(list.subList(0, 4)) { dl.fetch(sortBy = listOf(Person::name.asc), range = 0L..3L) }
            expectList(list[5]) { dl.fetch(buildFilter { Person::name eq "name 5" }, sortBy = listOf(Person::name.asc), range = 0L..2L) }
            expect(list.subList(3, 5)) { dl.fetch(buildFilter { Person::name ilike "name" }, sortBy = listOf(Person::name.asc), range = 3L..4L) }
        }
    }
})
