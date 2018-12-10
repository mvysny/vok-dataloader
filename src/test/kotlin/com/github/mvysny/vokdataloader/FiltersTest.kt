package com.github.mvysny.vokdataloader

import com.github.mvysny.dynatest.DynaTest
import kotlin.test.assertFailsWith
import kotlin.test.expect

class FiltersTest : DynaTest({
    test("not") {
        val eq = EqFilter<Person>(Person::name.name, "Name")
        val not = NotFilter(eq)

        expect(true) { not.test(Person("Name 5")) }
        expect(false) { not.test(Person("Name")) }

        expect("not (name = 'Name')") { not.toString() }
    }

    test("in") {
        val in1 = InFilter<Person>(Person::name.name, listOf("Name", "name 5"))
        expect("name in ('Name', 'name 5')") { in1.toString() }
        expect(true) { in1.test(Person("Name")) }
        expect(true) { in1.test(Person("name 5")) }
        expect(false) { in1.test(Person("name")) }
        expect(false) { in1.test(Person("Name 5")) }
        expect(false) { in1.test(Person("Na")) }
        expect(false) { in1.test(Person("na")) }
        expect(false) { in1.test(Person("")) }
        expect(false) { in1.test(Person(null)) }

        val in2 = InFilter<Person>(Person::name.name, listOf("name"))
        expect("name in ('name')") { in2.toString() }
        expect(true) { in2.test(Person("name")) }
        expect(false) { in2.test(Person("name 5")) }
        expect(false) { in2.test(Person("Name")) }
        expect(false) { in2.test(Person("Na")) }
        expect(false) { in2.test(Person("na")) }
        expect(false) { in1.test(Person(null)) }

        assertFailsWith(IllegalStateException::class) {
            InFilter<Person>(Person::name.name, listOf())
        }
    }

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

    group("build filter") {
        test("bean filters") {
            expect("name = '5'") { buildFilter<Person> { Person::name eq 5 } .toString() }
            expect("name < '5'") { buildFilter<Person> { Person::name lt 5 } .toString() }
            expect("name > '5'") { buildFilter<Person> { Person::name gt 5 } .toString() }
            expect("name >= '5'") { buildFilter<Person> { Person::name ge 5 } .toString() }
            expect("name LIKE 'foo%'") { buildFilter<Person> { Person::name like "foo" } .toString() }
            expect("name ILIKE 'bar%'") { buildFilter<Person> { Person::name ilike "bar" } .toString() }
            expect("name >= :foo{foo=foo}") { buildFilter<Person> { "name >= :foo"("foo" to "foo") } .toString() }
        }

        test("composed filters") {
            expect("(name < 'John' and age >= '5')") { buildFilter<Person> { (Person::name lt "John") and (Person::age ge 5) } .toString() }
            expect("(name < 'John' or age >= '5')") { buildFilter<Person> { (Person::name lt "John") or (Person::age ge 5) } .toString() }
        }
    }

    test("and") {
        val and = buildFilter<Person> { (Person::name lt "Name") and (Person::age ge 5) }
        expect(false) { and.test(Person("name 5", 2)) }
        expect(false) { and.test(Person("name 5", 10)) }
        expect(false) { and.test(Person("Na", 2)) }
        expect(true) { and.test(Person("Na", 5)) }
        expect(true) { and == buildFilter<Person> { (Person::name lt "Name") and (Person::age ge 5) } }
        expect(false) { and == buildFilter<Person> { (Person::name lt "Name") and (Person::age ge 10) } }
        expect(false) { and == buildFilter<Person> { (Person::name lt "Name a") and (Person::age ge 5) } }
    }

    test("or") {
        val or = buildFilter<Person> { (Person::name lt "Name") or (Person::age ge 5) }
        expect(false) { or.test(Person("name 5", 2)) }
        expect(true) { or.test(Person("name 5", 10)) }
        expect(true) { or.test(Person("Na", 2)) }
        expect(true) { or.test(Person("Na", 5)) }
        expect(true) { or == buildFilter<Person> { (Person::name lt "Name") or (Person::age ge 5) } }
        expect(false) { or == buildFilter<Person> { (Person::name lt "Name") and (Person::age ge 5) } }
        expect(false) { or == buildFilter<Person> { (Person::name lt "Name") or (Person::age ge 10) } }
        expect(false) { or == buildFilter<Person> { (Person::name lt "Name a") or (Person::age ge 5) } }
    }
})
