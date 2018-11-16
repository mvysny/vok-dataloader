package com.github.mvysny.vokdataloader

/**
 * Never returns any items.
 *
 * The native data set is always empty; therefore there is no data row and the [NativePropertyName] set is empty
 * (there are no native properties). This loader accepts anything as [DataLoaderPropertyName]. The filters and
 * sort clauses are always ignored.
 *
 * There is no mapping-to-Java-Bean going on since nothing is returned.
 */
class EmptyDataLoader<T: Any>: DataLoader<T> {
    override fun getCount(filter: Filter<T>?): Long = 0L
    override fun fetch(filter: Filter<T>?, sortBy: List<SortClause>, range: LongRange): List<T> = listOf()
    override fun toString(): String = "EmptyDataLoader"
}