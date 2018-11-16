package com.github.mvysny.vokdataloader

/**
 * Never returns any items.
 */
class EmptyDataLoader<T: Any>: DataLoader<T> {
    override fun getCount(filter: Filter<T>?): Long = 0L
    override fun fetch(filter: Filter<T>?, sortBy: List<SortClause>, range: LongRange): List<T> = listOf()
    override fun toString(): String = "EmptyDataLoader"
}