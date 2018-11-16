package com.github.mvysny.vokdataloader

import java.io.Serializable
import java.lang.reflect.Method
import java.util.function.BiPredicate
import java.util.function.Predicate
import kotlin.reflect.KProperty1

/**
 * A generic filter which filters items of type [T]. Implementors must precisely declare in kdoc how exactly the items are filtered.
 *
 * Implementor detail: [Any.equals]/[Any.hashCode]/[Any.toString] must be implemented properly, so that the filters can be
 * placed in a set. As a bare minimum, the filter type, the property name and the value which we compare against must be
 * taken into consideration.
 *
 * The [test] method is provided only as a convenience. There may be filters which can not implement this kind of method
 * (e.g. REST-server-specific filters, or filters representing a SQL where clause). Such filters should document this
 * behavior and may throw [UnsupportedOperationException] in the [test] method.
 * @param T the bean type upon which we will perform the filtering. This is not used by the filter directly, but it's required by [Predicate] which this
 * interface extends.
 */
interface Filter<T: Any> : Serializable, Predicate<T> {
    infix fun and(other: Filter<in T>): Filter<T> = AndFilter(setOf(this, other))
    infix fun or(other: Filter<in T>): Filter<T> = OrFilter(setOf(this, other))
}

/**
 * Filters beans by comparing given [propertyName] to some expected [value]. Check out implementors for further details.
 * @property propertyName the bean property name ([KProperty1.name]).
 * @property value optional value to compare against.
 */
abstract class BeanFilter<T: Any> : Filter<T> {
    abstract val propertyName: DataLoaderPropertyName
    abstract val value: Any?
    @Transient
    private var getter: Method? = null

    /**
     * Gets the value of the [propertyName] for given [bean]. Very quick even though it uses reflection under the hood.
     */
    protected fun getValue(bean: T): Any? {
        var g = getter
        if (g == null) {
            g = bean.javaClass.getGetter(propertyName)
            getter = g
        }
        return g.invoke(bean)
    }

    protected val formattedValue: String? get() = if (value != null) "'$value'" else null
}

/**
 * A filter which tests for value equality. Allows nulls.
 */
data class EqFilter<T: Any>(override val propertyName: DataLoaderPropertyName, override val value: Any?) : BeanFilter<T>() {
    override fun toString() = "$propertyName = $formattedValue"
    override fun test(t: T): Boolean = getValue(t) == value
}

enum class CompareOperator(val sql92Operator: String) : BiPredicate<Comparable<Any>?, Comparable<*>> {
    eq("=") { override fun test(t: Comparable<Any>?, u: Comparable<*>) = t == u },
    lt("<") { override fun test(t: Comparable<Any>?, u: Comparable<*>) = t == null || t < u },
    le("<=") { override fun test(t: Comparable<Any>?, u: Comparable<*>) = t == null || t <= u },
    gt(">") { override fun test(t: Comparable<Any>?, u: Comparable<*>) = t != null && t > u },
    ge(">=") { override fun test(t: Comparable<Any>?, u: Comparable<*>) = t != null && t >= u },
}

/**
 * A filter which supports less than, less or equals than, etc. Filters out null values.
 */
data class OpFilter<T: Any>(override val propertyName: DataLoaderPropertyName, override val value: Comparable<*>, val operator: CompareOperator) : BeanFilter<T>() {
    override fun toString() = "$propertyName ${operator.sql92Operator} $formattedValue"
    @Suppress("UNCHECKED_CAST")
    override fun test(t: T): Boolean = operator.test(getValue(t) as Comparable<Any>?, value)
}

data class IsNullFilter<T: Any>(override val propertyName: DataLoaderPropertyName) : BeanFilter<T>() {
    override val value: Any? = null
    override fun test(t: T): Boolean = getValue(t) == null
    override fun toString(): String = "$propertyName IS NULL"
}

data class IsNotNullFilter<T: Any>(override val propertyName: DataLoaderPropertyName) : BeanFilter<T>() {
    override fun test(t: T): Boolean = getValue(t) != null
    override val value: Any? = null
    override fun toString(): String = "$propertyName IS NOT NULL"
}

/**
 * A LIKE filter. It performs the 'starts-with' matching which tends to perform quite well on indexed columns. If you need a substring
 * matching, then you actually need to employ full text search
 * capabilities of your database. For example [PostgreSQL full-text search](https://www.postgresql.org/docs/9.5/static/textsearch.html).
 *
 * There is no point in supporting substring matching: it performs a full table scan when used, regardless of whether the column contains
 * the index or not. If you really wish for substring matching, you probably want a full-text search instead which is implemented using
 * a different keywords.
 * @param startsWith the prefix, automatically appended with `%` when the SQL query is constructed. The 'starts-with' is matched
 * case-sensitive.
 */
class LikeFilter<T: Any>(override val propertyName: DataLoaderPropertyName, startsWith: String) : BeanFilter<T>() {
    val startsWith = startsWith.trim()
    override val value = "${this.startsWith}%"
    override fun toString() = "$propertyName LIKE $formattedValue"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as LikeFilter<*>
        if (propertyName != other.propertyName) return false
        if (value != other.value) return false
        return true
    }
    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }

    override fun test(t: T): Boolean {
        val v = getValue(t) as? String ?: return false
        return v.startsWith(startsWith)
    }
}

/**
 * An ILIKE filter, performs case-insensitive matching. It performs the 'starts-with' matching which tends to perform quite well on indexed columns. If you need a substring
 * matching, then you actually need to employ full text search
 * capabilities of your database. For example [PostgreSQL full-text search](https://www.postgresql.org/docs/9.5/static/textsearch.html).
 *
 * There is no point in supporting substring matching: it performs a full table scan when used, regardless of whether the column contains
 * the index or not. If you really wish for substring matching, you probably want a full-text search instead which is implemented using
 * a different keywords.
 * @param startsWith the prefix, automatically appended with `%` when the SQL query is constructed. The 'starts-with' is matched
 * case-insensitive.
 */
class ILikeFilter<T: Any>(override val propertyName: DataLoaderPropertyName, startsWith: String) : BeanFilter<T>() {
    val startsWith = startsWith.trim()
    override val value = "${this.startsWith}%"
    override fun toString() = "$propertyName ILIKE $formattedValue"
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as ILikeFilter<*>
        if (propertyName != other.propertyName) return false
        if (value != other.value) return false
        return true
    }
    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + value.hashCode()
        return result
    }
    override fun test(t: T): Boolean {
        val v = getValue(t) as? String ?: return false
        return v.startsWith(startsWith, ignoreCase = true)
    }
}

class AndFilter<T: Any>(children: Set<Filter<in T>>) : Filter<T> {
    val children: Set<Filter<in T>> = children.flatMap { if (it is AndFilter) it.children else listOf(it) }.toSet()
    override fun toString() = children.joinToString(" and ", "(", ")")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as AndFilter<*>
        if (children != other.children) return false
        return true
    }
    override fun hashCode() = children.hashCode()
    override fun test(t: T): Boolean = children.all { it.test(t) }
}

class OrFilter<T: Any>(children: Set<Filter<in T>>) : Filter<T> {
    val children: Set<Filter<in T>> = children.flatMap { if (it is OrFilter) it.children else listOf(it) }.toSet()
    override fun toString() = children.joinToString(" or ", "(", ")")
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        other as OrFilter<*>
        if (children != other.children) return false
        return true
    }
    override fun hashCode() = children.hashCode()
    override fun test(t: T): Boolean = children.any { it.test(t) }
}

fun <T: Any> Set<Filter<T>>.and(): Filter<T>? = when (size) {
    0 -> null
    1 -> first()
    else -> AndFilter(this)
}
fun <T: Any> Set<Filter<T>>.or(): Filter<T>? = when (size) {
    0 -> null
    1 -> first()
    else -> OrFilter(this)
}

/**
 * Running block with this class as its receiver will allow you to write expressions like this:
 * `Person::age lt 25`. Does not support joins - just use the plain old SQL 92 where syntax for that ;)
 *
 * Containing these functions in this class will prevent polluting of the KProperty1 interface and also makes it type-safe.
 *
 * This looks like too much Kotlin syntax magic. Promise me to use this for simple Entities and/or programmatic where creation only ;)
 * @param clazz builds the query for this class.
 */
class SqlWhereBuilder<T: Any>(val clazz: Class<T>) {
    infix fun <R: Serializable?> KProperty1<T, R>.eq(value: R): Filter<T> = EqFilter(name, value)
    @Suppress("UNCHECKED_CAST")
    infix fun <R> KProperty1<T, R?>.le(value: R): Filter<T> =
        OpFilter(name, value as Comparable<Any>, CompareOperator.le)
    @Suppress("UNCHECKED_CAST")
    infix fun <R> KProperty1<T, R?>.lt(value: R): Filter<T> =
        OpFilter(name, value as Comparable<Any>, CompareOperator.lt)
    @Suppress("UNCHECKED_CAST")
    infix fun <R> KProperty1<T, R?>.ge(value: R): Filter<T> =
        OpFilter(name, value as Comparable<Any>, CompareOperator.ge)
    @Suppress("UNCHECKED_CAST")
    infix fun <R> KProperty1<T, R?>.gt(value: R): Filter<T> =
        OpFilter(name, value as Comparable<Any>, CompareOperator.gt)

    /**
     * A LIKE filter. It performs the 'starts-with' matching which tends to perform quite well on indexed columns. If you need a substring
     * matching, then you actually need to employ full text search
     * capabilities of your database. For example [PostgreSQL full-text search](https://www.postgresql.org/docs/9.5/static/textsearch.html).
     *
     * There is no point in supporting substring matching: it performs a full table scan when used, regardless of whether the column contains
     * the index or not. If you really wish for substring matching, you probably want a full-text search instead which is implemented using
     * a different keywords.
     * @param prefix the prefix, automatically appended with `%` when the SQL query is constructed. The 'starts-with' is matched
     * case-sensitive.
     */
    infix fun KProperty1<T, String?>.like(prefix: String): Filter<T> = LikeFilter(name, prefix)

    /**
     * An ILIKE filter, performs case-insensitive matching. It performs the 'starts-with' matching which tends to perform quite well on indexed columns. If you need a substring
     * matching, then you actually need to employ full text search
     * capabilities of your database. For example [PostgreSQL full-text search](https://www.postgresql.org/docs/9.5/static/textsearch.html).
     *
     * There is no point in supporting substring matching: it performs a full table scan when used, regardless of whether the column contains
     * the index or not. If you really wish for substring matching, you probably want a full-text search instead which is implemented using
     * a different keywords.
     * @param prefix the prefix, automatically appended with `%` when the SQL query is constructed. The 'starts-with' is matched
     * case-insensitive.
     */
    infix fun KProperty1<T, String?>.ilike(prefix: String): Filter<T> = ILikeFilter(name, prefix)
    /**
     * Matches only values contained in given range.
     * @param range the range
     */
    infix fun <R> KProperty1<T, R?>.between(range: ClosedRange<R>): Filter<T> where R: Number, R: Comparable<R> =
            this.ge(range.start as Number) and this.le(range.endInclusive as Number)
    val KProperty1<T, *>.isNull: Filter<T> get() = IsNullFilter(name)
    val KProperty1<T, *>.isNotNull: Filter<T> get() = IsNotNullFilter(name)
    val KProperty1<T, Boolean?>.isTrue: Filter<T> get() = EqFilter(name, true)
    val KProperty1<T, Boolean?>.isFalse: Filter<T> get() = EqFilter(name, false)

    /**
     * Allows for a native query: `"age < :age_p"("age_p" to 60)`
     */
    operator fun String.invoke(vararg params: Pair<String, Any?>) = NativeSqlFilter<T>(this, mapOf(*params))
}

/**
 * Just write any native SQL into [where], e.g. `age > 25 and name like :name`; don't forget to properly fill in the [params] map.
 *
 * Does not support in-memory filtering and will throw an exception.
 */
data class NativeSqlFilter<T: Any>(val where: String, val params: Map<String, Any?>) : Filter<T> {
    override fun test(t: T): Boolean = throw UnsupportedOperationException("Does not support in-memory filtering")
}

/**
 * Creates a filter programmatically: `buildFilter { Person::age lt 25 }`
 */
inline fun <reified T: Any> buildFilter(block: SqlWhereBuilder<T>.()-> Filter<T>): Filter<T> = block(SqlWhereBuilder(T::class.java))
