package com.github.mvysny.vokdataloader

import java.beans.Introspector
import java.lang.reflect.Method

/**
 * Returns a getter method for given [propertyName] for this class. Fails if there is no such property, or if the
 * property is write-only (it doesn't have a getter).
 */
fun Class<*>.getGetter(propertyName: String): Method {
    val propertyDescriptor = Introspector.getBeanInfo(this).propertyDescriptors.firstOrNull { it.name == propertyName }
            ?: throw IllegalStateException("Bean $this has no property $propertyName")
    return propertyDescriptor.readMethod
            ?: throw IllegalStateException("Bean $this has no readMethod for property $propertyDescriptor")
}
