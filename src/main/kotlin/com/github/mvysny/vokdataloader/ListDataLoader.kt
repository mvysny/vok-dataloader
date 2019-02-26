package com.github.mvysny.vokdataloader

/**
 * A simple in-memory data loader which provides beans from given [items] list.
 *
 * The [NativePropertyName] is always the Java Bean Property name. The [DataLoaderPropertyName]s accepted for filters
 * and sort clauses are always the Java Bean Property names. Only valid Bean Property Names for bean of type [itemClass]
 * may be passed in.
 *
 * Uses identity as mapping: turns a Java Bean into ... well, Java Bean ;)
 *
 * Thread-safe.
 * @property itemClass every item is of this type. Used to lookup getters for Java Bean Properties.
 * @property items provide instances of these items.
 */
class ListDataLoader<T: Any>(val itemClass: Class<T>, val items: List<T>) : DataLoader<T> {

    private fun filter(filter: Filter<T>?): List<T> = if (filter == null) items else items.filter { filter.test(it) }

    override fun getCount(filter: Filter<T>?): Long = filter(filter).size.toLong()

    override fun fetch(filter: Filter<T>?, sortBy: List<SortClause>, range: LongRange): List<T> {
        if (range.isEmpty()) return listOf()
        var list = filter(filter)
        list = list.sortedBy(sortBy, itemClass)
        return list.subList(range.intRange.intersection(list.indices))
    }

    override fun toString() = "ListDataLoader($items)"
}

/**
 * Returns [ListDataLoader] which loads given list. It may not reflect items added to the list post-construction
 * of the loader (e.g. it optimizes by returning [EmptyDataLoader] when called on an empty list).
 */
inline fun <reified T: Any> List<T>.dataLoader(): DataLoader<T> = when {
    isEmpty() -> EmptyDataLoader()
    else -> ListDataLoader(T::class.java, this)
}

/**
 * Creates a [Comparator] which compares items of given [itemClass] by [SortClause.propertyName]. Reads the property
 * value using Java Reflection. Useful for doing in-memory comparisons.
 */
fun SortClause.getComparator(itemClass: Class<*>): Comparator<Any> {
    val getter = itemClass.getGetter(propertyName)
    val comparator: Comparator<Any> = compareBy { bean -> getter.invoke(bean) as Comparable<*>? }
    return if (asc) comparator else comparator.reversed()
}

/**
 * Creates a [Comparator] which compares items by all comparators in this list. If the list is empty, the comparator
 * will always treat all items as equal and will return `0`.
 */
fun <T> List<Comparator<T>>.toComparator(): Comparator<T> = when {
    isEmpty() -> Comparator { _, _ -> 0 }
    size == 1 -> first()
    else -> ComparatorList(this)
}

private class ComparatorList<T>(val comparators: List<Comparator<T>>) : Comparator<T> {
    override fun compare(o1: T, o2: T): Int {
        for (comparator in comparators) {
            val result = comparator.compare(o1, o2)
            if (result != 0) return result
        }
        return 0
    }
}

/**
 * Returns a copy of this list, sorted in-memory according to given [criteria]. [itemClass] is used for reflection so that we can obtain
 * the values of given property for all beans.
 */
fun <T> List<T>.sortedBy(criteria: List<SortClause>, itemClass: Class<T>): List<T> = when {
    criteria.isEmpty() || isEmpty() -> this
    else -> {
        val comparator: Comparator<Any> = criteria.map { it.getComparator(itemClass) } .toComparator()
        sortedWith(nullsFirst<Any>(comparator))
    }
}
