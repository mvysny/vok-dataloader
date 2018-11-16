package com.github.mvysny.vokdataloader

import java.lang.reflect.Method
import java.util.concurrent.ConcurrentHashMap

/**
 * A simple in-memory data loader which provides beans from given [items] list.
 *
 * The [NativePropertyName] is always the Java Bean Property names. The [DataLoaderPropertyName]s accepted for filters
 * and sort clauses are always the Java Bean Property names.
 *
 * Thread-safe.
 */
class ListDataLoader<T: Any>(val itemClass: Class<T>, val items: List<T>) : DataLoader<T> {

    private fun filter(filter: Filter<T>?): List<T> = if (filter == null) items else items.filter { filter.test(it) }

    override fun getCount(filter: Filter<T>?): Long = filter(filter).size.toLong()

    private val getterCache = ConcurrentHashMap<DataLoaderPropertyName, Method>()
    private fun getGetter(prop: DataLoaderPropertyName): Method =
            getterCache.computeIfAbsent(prop) { itemClass.getGetter(prop) }

    private val comparatorCache = ConcurrentHashMap<SortClause, Comparator<Any>>()
    private fun getComparator(sortClause: SortClause): Comparator<Any> = comparatorCache.computeIfAbsent(sortClause) { it ->
        val getter = getGetter(it.propertyName)
        val comparator: Comparator<Any> = compareBy { bean -> getter.invoke(bean) as Comparable<*>? }
        if (it.asc) comparator else comparator.reversed()
    }

    private fun sort(list: List<T>, criteria: List<SortClause>): List<T> {
        if (criteria.isEmpty()) return list
        val comparator: Comparator<Any> = criteria.map { getComparator(it) } .toComparator()
        return list.sortedWith(comparator)
    }

    private fun List<Comparator<Any>>.toComparator() = object : Comparator<Any> {
        override fun compare(o1: Any?, o2: Any?): Int {
            for (comparator in this@toComparator) {
                val result = comparator.compare(o1, o2)
                if (result != 0) return result
            }
            return 0
        }
    }

    override fun fetch(filter: Filter<T>?, sortBy: List<SortClause>, range: LongRange): List<T> {
        if (range.isEmpty()) return listOf()
        var list = filter(filter)
        list = sort(list, sortBy)
        val rangeEndExclusive = range.endInclusive.coerceAtMost(Long.MAX_VALUE - 1) + 1
        return list.subList(range.start.coerceAtMost(list.size.toLong()).toInt(),
                rangeEndExclusive.coerceAtMost(list.size.toLong()).toInt())
    }

    override fun toString() = "ListDataLoader($items)"
}

/**
 * Returns [ListDataLoader] which loads given list. It may not reflect on items added to the list post-construction
 * of the loader.
 */
inline fun <reified T: Any> List<T>.dataLoader(): DataLoader<T> = when {
    isEmpty() -> EmptyDataLoader()
    else -> ListDataLoader(T::class.java, this)
}
