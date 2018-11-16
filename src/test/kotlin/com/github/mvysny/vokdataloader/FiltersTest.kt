package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import kotlin.test.expect
import kotlin.test.fail

class FiltersTest : DynaTest({
    test("todo") {
        fail("todo")
    }
    test("ilike") {
        val ilike = ILikeFilter<Person>(Person::name.name, "Name")
        expect(true) { ilike.test(Person("name 5")) }
        expect(true) { ilike.test(Person("name")) }
        expect(true) { ilike.test(Person("Name 5")) }
        expect(true) { ilike.test(Person("Name")) }
        expect(false) { ilike.test(Person("Na")) }
        expect(false) { ilike.test(Person("na")) }
        expect(false) { ilike.test(Person("")) }
        expect("""name ILIKE "Name%"""") { ilike.toString() }
        expect(true) { ilike == ILikeFilter<Person>(Person::name.name, "Name") }
        expect(false) { ilike == ILikeFilter<Person>(Person::name.name, "namea") }
    }
})
