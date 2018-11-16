package com.github.mvysny.vokdataloader

import java.io.Serializable

/**
 * Accesses a pageable/filtrable/sortable native data set of some sort. For example, a SQL data loader will run SELECT, take
 * the JDBC ResultSet and convert every row into a Java Bean. Another example would be a REST endpoint data loader which
 * takes a list of JSON maps and converts every row (=JSON map) into a Java Bean.
 *
 * The native data set is expected to contain zero, one or more native data rows. The data loader retrieves native data rows
 * and turns them into instances of bean of type [T] (one data row into one bean).
 *
 * Typically provides data for some kind of a scrollable/paged table, for example Vaadin Grid.
 *
 * Implementor classes must document:
 * * What is the data row and what kind of backend this loader fetches data from;
 * * What is [NativePropertyName] for this data loader,
 * * What kind of properties they accept for filters and sort clauses (what is the [DataLoaderPropertyName] set).
 * * How exactly [NativePropertyName]s (e.g. JSON map keys, or SQL SELECT column names) are mapped to Java Bean Properties,
 *   and what is the mechanism to possibly alter this mapping.
 * * Provide a proper [Any.toString] to inform what kind of items this data loader offers.
 * * [Any.hashCode]/[Any.equals] are not important and may not be implemented.
 *
 * For more information on this topic see [NativePropertyName] and [DataLoaderPropertyName].
 */
interface DataLoader<T: Any> : Serializable {
    /**
     * Returns the number of items available which match given [filter].
     * @param filter optional filter which defines filtering to be used for counting the
     * number of items. If null all items are considered.
     */
    fun getCount(filter: Filter<T>? = null): Long

    /**
     * Fetches data from the back end. The items must match given [filter], then be sorted according to given [sortBy]
     * clause; then only return given [range] of that outcome.
     *
     * The `fetch()` function should never fail with e.g. `IndexOutOfBoundsException` if the range is out-of-bounds; instead
     * it should simply return fewer items (or no items at all). It is the callee responsibility to input proper ranges
     * according to the outcome of the [getCount] function.
     *
     * @param filter optional filter which defines filtering to be used for counting the
     * number of items. If null all items are considered.
     * @param sortBy optionally sort the beans according to given criteria.
     * @param range offset and limit to fetch. The range may be empty, in that case an empty list should simply be returned.
     * The [LongRange.start] must be 0 or higher.
     * @return a list of items matching the query, may be empty. It is allowed to return less items than requested by
     * [range] if there is not enough items, or if the range is out of bounds etc.
     */
    fun fetch(filter: Filter<T>? = null, sortBy: List<SortClause> = listOf(), range: LongRange = 0..Long.MAX_VALUE): List<T>
}

/**
 * Returns a new data loader which always applies given [filter] and ANDs it with any filter given to [DataLoader.getCount] or [DataLoader.fetch].
 */
fun <T: Any> DataLoader<T>.withFilter(filter: Filter<T>): DataLoader<T> = FilteredDataLoader(filter, this)

/**
 * Returns a new data loader which always applies given [filter] and ANDs it with any filter given to [DataLoader.getCount] or [DataLoader.fetch].
 */
inline fun <reified T: Any> DataLoader<T>.withFilter(block: SqlWhereBuilder<T>.()->Filter<T>): DataLoader<T> =
    withFilter(SqlWhereBuilder(T::class.java).block())

/**
 * Wraps [delegate] data loader and always applies given [filter].
 */
class FilteredDataLoader<T: Any>(val filter: Filter<T>, val delegate: DataLoader<T>) : DataLoader<T> {
    private fun and(other: Filter<T>?) = if (other == null) filter else filter.and(other)

    override fun getCount(filter: Filter<T>?): Long = delegate.getCount(and(filter))

    override fun fetch(filter: Filter<T>?, sortBy: List<SortClause>, range: LongRange): List<T> =
            delegate.fetch(and(filter), sortBy, range)

    override fun toString(): String = "FilteredDataLoader($delegate, filter=$filter)"
}
