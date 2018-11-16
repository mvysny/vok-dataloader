package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import kotlin.test.expect

class FiltersTest : DynaTest({
    test("like") {
        val like = LikeFilter<Person>(Person::name.name, "Name")
        expect(false) { like.test(Person("name 5")) }
        expect(false) { like.test(Person("name")) }
        expect(true) { like.test(Person("Name 5")) }
        expect(true) { like.test(Person("Name")) }
        expect(false) { like.test(Person("Na")) }
        expect(false) { like.test(Person("na")) }
        expect(false) { like.test(Person("")) }
        expect(false) { like.test(Person(null)) }
        expect("name LIKE 'Name%'") { like.toString() }
        expect(true) { like == LikeFilter<Person>(Person::name.name, "Name") }
        expect(false) { like == LikeFilter<Person>(Person::name.name, "namea") }
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
        expect(false) { ilike.test(Person(null)) }
        expect("name ILIKE 'Name%'") { ilike.toString() }
        expect(true) { ilike == ILikeFilter<Person>(Person::name.name, "Name") }
        expect(false) { ilike == ILikeFilter<Person>(Person::name.name, "namea") }
    }

    test("isnull") {
        val isnull = IsNullFilter<Person>(Person::name.name)
        expect(false) { isnull.test(Person("name 5")) }
        expect(false) { isnull.test(Person("name")) }
        expect(false) { isnull.test(Person("Name 5")) }
        expect(false) { isnull.test(Person("Name")) }
        expect(false) { isnull.test(Person("Na")) }
        expect(false) { isnull.test(Person("na")) }
        expect(false) { isnull.test(Person("")) }
        expect(true) { isnull.test(Person(null)) }
        expect("name IS NULL") { isnull.toString() }
        expect(true) { isnull == IsNullFilter<Person>(Person::name.name) }
        expect(false) { isnull == IsNullFilter<Person>("surname") }
    }

    test("isnotnull") {
        val isnull = IsNotNullFilter<Person>(Person::name.name)
        expect(true) { isnull.test(Person("name 5")) }
        expect(true) { isnull.test(Person("name")) }
        expect(true) { isnull.test(Person("Name 5")) }
        expect(true) { isnull.test(Person("Name")) }
        expect(true) { isnull.test(Person("Na")) }
        expect(true) { isnull.test(Person("na")) }
        expect(true) { isnull.test(Person("")) }
        expect(false) { isnull.test(Person(null)) }
        expect("name IS NOT NULL") { isnull.toString() }
        expect(true) { isnull == IsNotNullFilter<Person>(Person::name.name) }
        expect(false) { isnull == IsNotNullFilter<Person>("surname") }
    }

    test("eq") {
        val eq = EqFilter<Person>(Person::name.name, "Name")
        expect(false) { eq.test(Person("name 5")) }
        expect(false) { eq.test(Person("name")) }
        expect(false) { eq.test(Person("Name 5")) }
        expect(true) { eq.test(Person("Name")) }
        expect(false) { eq.test(Person("Na")) }
        expect(false) { eq.test(Person("na")) }
        expect(false) { eq.test(Person("")) }
        expect(false) { eq.test(Person(null)) }
        expect("name = 'Name'") { eq.toString() }
        expect(true) { eq == EqFilter<Person>(Person::name.name, "Name") }
        expect(false) { eq == EqFilter<Person>(Person::name.name, "namea") }
    }

    group("op") {
        test("eq") {
            val eq = OpFilter<Person>(Person::name.name, "Name", CompareOperator.eq)
            expect(false) { eq.test(Person("name 5")) }
            expect(false) { eq.test(Person("name")) }
            expect(false) { eq.test(Person("Name 5")) }
            expect(true) { eq.test(Person("Name")) }
            expect(false) { eq.test(Person("Na")) }
            expect(false) { eq.test(Person("na")) }
            expect(false) { eq.test(Person("")) }
            expect(false) { eq.test(Person(null)) }
            expect("name = 'Name'") { eq.toString() }
            expect(true) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.eq) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "namea", CompareOperator.eq) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.ge) }
        }
        test("lt") {
            val eq = OpFilter<Person>(Person::name.name, "Name", CompareOperator.lt)
            expect(false) { eq.test(Person("name 5")) }
            expect(false) { eq.test(Person("name")) }
            expect(false) { eq.test(Person("Name 5")) }
            expect(false) { eq.test(Person("Name")) }
            expect(true) { eq.test(Person("Na")) }
            expect(false) { eq.test(Person("na")) }
            expect(true) { eq.test(Person("")) }
            expect(true) { eq.test(Person(null)) }
            expect("name < 'Name'") { eq.toString() }
            expect(true) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.lt) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "namea", CompareOperator.lt) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.eq) }
        }
        test("le") {
            val eq = OpFilter<Person>(Person::name.name, "Name", CompareOperator.le)
            expect(false) { eq.test(Person("name 5")) }
            expect(false) { eq.test(Person("name")) }
            expect(false) { eq.test(Person("Name 5")) }
            expect(true) { eq.test(Person("Name")) }
            expect(true) { eq.test(Person("Na")) }
            expect(false) { eq.test(Person("na")) }
            expect(true) { eq.test(Person("")) }
            expect(true) { eq.test(Person(null)) }
            expect("name <= 'Name'") { eq.toString() }
            expect(true) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.le) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "namea", CompareOperator.le) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.eq) }
        }
        test("ge") {
            val eq = OpFilter<Person>(Person::name.name, "Name", CompareOperator.ge)
            expect(true) { eq.test(Person("name 5")) }
            expect(true) { eq.test(Person("name")) }
            expect(true) { eq.test(Person("Name 5")) }
            expect(true) { eq.test(Person("Name")) }
            expect(false) { eq.test(Person("Na")) }
            expect(true) { eq.test(Person("na")) }
            expect(false) { eq.test(Person("")) }
            expect(false) { eq.test(Person(null)) }
            expect("name >= 'Name'") { eq.toString() }
            expect(true) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.ge) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "namea", CompareOperator.ge) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.eq) }
        }
        test("gt") {
            val eq = OpFilter<Person>(Person::name.name, "Name", CompareOperator.gt)
            expect(true) { eq.test(Person("name 5")) }
            expect(true) { eq.test(Person("name")) }
            expect(true) { eq.test(Person("Name 5")) }
            expect(false) { eq.test(Person("Name")) }
            expect(false) { eq.test(Person("Na")) }
            expect(true) { eq.test(Person("na")) }
            expect(false) { eq.test(Person("")) }
            expect(false) { eq.test(Person(null)) }
            expect("name > 'Name'") { eq.toString() }
            expect(true) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.gt) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "namea", CompareOperator.gt) }
            expect(false) { eq == OpFilter<Person>(Person::name.name, "Name", CompareOperator.eq) }
        }
    }
})
