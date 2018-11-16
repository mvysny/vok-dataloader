package com.github.mvysny.vokdataloader

import java.io.Serializable
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * Specifies a sorting clause, sorting by given [propertyName], ascending or descending based on the value of the [asc] parameter.
 * @property propertyName the bean property name ([KProperty1.name]).
 */
data class SortClause(val propertyName: DataLoaderPropertyName, val asc: Boolean) : Serializable {
    init {
        require(propertyName.isNotBlank()) { "propertyName must not be blank" }
    }
}

val DataLoaderPropertyName.asc: SortClause get() = SortClause(this, true)
val DataLoaderPropertyName.desc: SortClause get() = SortClause(this, false)
val KProperty<*>.asc: SortClause get() = name.asc
val KProperty<*>.desc: SortClause get() = name.desc
