package com.github.mvysny.vokdataloader

import java.io.Serializable

/**
 * Provides paged access to instances of bean of type [T]. Typically provides data for some kind of a scrollable/paged table.
 *
 * The data loader takes a data row and converts it into a Java Bean. For example, a SQL data loader will run SELECT, take
 * the JDBC ResultSet and convert every row into a Java Bean. Another example would be a REST endpoint data loader which
 * takes a list of JSON maps and converts every row (=JSON map) into a Java Bean.
 *
 * Implementor classes must document:
 * * What kind of properties they accept for filters and sort clauses
 * * How exactly the data row properties (e.g. JSON map keys, or SQL SELECT column names) are mapped to Java Bean Properties.
 *
 * For more information on this topic see [NativePropertyName].
 */
interface DataLoader<T: Any> : Serializable {
    /**
     * Returns the number of items available which match given [filter].
     * @param filter optional filter which defines filtering to be used for counting the
     * number of items. If null all items are considered.
     */
    fun getCount(filter: Filter<T>? = null): Long

    /**
     * Fetches data from the back end. The items must match given [filter]
     * @param filter optional filter which defines filtering to be used for counting the
     * number of items. If null all items are considered.
     * @param sortBy optionally sort the beans according to given criteria.
     * @param range offset and limit to fetch
     * @return a list of items matching the query, may be empty.
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
}
