package com.github.mvysny.vokdataloader

/**
 * Wraps a [DataLoader] and provides a random-access list interface. This class keeps a cache of pages around most
 * recently retrieved item; pages are loaded from the [loader] as necessary.
 *
 * At most three pages are cached. Not thread-safe. You can use this class to feed [DataLoader] to Android's `ListAdapter`.
 * @param pageSize the size of the page. If this list will be used to display on-screen data, the best size of the page
 * is the number of items visible at once in the scrolling view.
 */
class PagedList<T: Any>(val loader: DataLoader<T>, val pageSize: Int): AbstractList<T>() {
    private val pages = mutableMapOf<Int, List<T>>()
    init {
        require(pageSize >= 1) { "pageSize must be 1 or greater: $pageSize"}
    }
    override val size: Int
        get() = loader.getCount(null).toInt()

    override fun get(index: Int): T {
        require(index >= 0) { "index must be 0 or greater: $index" }
        val pageIndex = index / pageSize
        pages.keys.retainAll(setOf(pageIndex - 1, pageIndex, pageIndex + 1))
        cachePage(pageIndex - 1)
        val page = cachePage(pageIndex)
        cachePage(pageIndex + 1)
        return page[index % pageSize]
    }

    private fun cachePage(pageIndex: Int): List<T> = pages.computeIfAbsent(pageIndex) {
        val startIndex = pageIndex * pageSize
        loader.fetch(range = startIndex.toLong()..(startIndex + pageSize - 1).toLong())
    }

    /**
     * A read-only view of the current cache. Maps page index to the list of items retrieved from the [loader].
     */
    val cache: Map<Int, List<T>> get() = pages
}
