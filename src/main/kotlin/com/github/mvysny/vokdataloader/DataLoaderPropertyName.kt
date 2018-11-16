package com.github.mvysny.vokdataloader

/**
 * A name of a single property of the native raw data row as loaded by the [DataLoader].
 *
 * For example:
 * * A SQL data loader loads from an outcome of a SQL SELECT, via JDBC's [java.sql.ResultSet]. The data row
 *   in this case is a single row in the SQL SELECT which is a collection of database columns. The native property names are
 *   for example a database table column name (`PERSON_NAME`), or a column reference (`p.person_name` in
 *   `select p.person_name from Person p`).
 * * For a REST data loader this follows the REST endpoint naming scheme of the JSON maps returned via the GET.
 *   For example if the REST returns `[{"person_name": "John"}]`,
 *   then the row contains the property of `person_name`.
 * * For an in-memory collection of Java Beans, the native property name is already the Java Bean Property name, and
 *   therefore there is no distinction to [DataLoaderPropertyName] in this case.
 */
typealias NativePropertyName = String

/**
 * A name of a single property that the [DataLoader] accepts as a filter property name, or a sort clause property name.
 *
 * Since every data loader produces Java Beans, every [DataLoader] MUST support Java Bean Property names
 * as [DataLoaderPropertyName]. In addition to that, the DataLoader MAY decide to
 * additionally accept [NativePropertyName]s as [DataLoaderPropertyName]s, to allow the user to reference native properties in filters and
 * sort clauses which aren't mapped to the Java Bean.
 * Every [DataLoader] MUST document what exactly he accepts (and what he doesn't accept).
 *
 * For example:
 *
 * * [vok-orm](https://github.com/mvysny/vok-orm) maps SQL SELECT outcome to Java/Kotlin class. It needs to map
 * [DataLoaderPropertyName] to [NativePropertyName] manually (can't use SQL aliases to map [DataLoaderPropertyName] to [NativePropertyName]
 * e.g. `select p.person_name as personName`) since SQL databases can not create WHERE
 * clauses based on aliases: [vok-orm issue 5](https://github.com/mvysny/vok-orm/issues/5). `vok-orm` is therefore
 * using the `@As` annotation on the Java Bean Property. In addition however we must support sorting and filtering based on
 * [NativePropertyName] to allow filtering on columns not returned/mapped to Java Bean; in this case the native property name
 * is for example a database table column name (`PERSON_NAME`), or a column reference (`p.person_name` in
 *   `select p.person_name from Person p`).
 * * [vok-rest-client](https://github.com/mvysny/vaadin-on-kotlin/tree/master/vok-rest-client) uses Gson to turn list of
 * JSON maps into a list of Java Beans. It is a good practice for the REST filter names and the sorting criteria property naming
 * to follow names of keys in the JSON maps, and hence we will most probably have a Java Bean Property for every filter
 * or sort clause we can have. REST endpoints may decide to use the `lowercase_underscore` (or any other) naming scheme;
 * it is therefore the responsibility of REST data loader to e.g. to configure Gson to use column name aliases, for example using the `@As` annotation.
 */
typealias DataLoaderPropertyName = String
