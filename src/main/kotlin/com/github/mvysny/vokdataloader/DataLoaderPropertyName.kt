package com.github.mvysny.vokdataloader

/**
 * A name of a single property of the native raw data row as loaded by the [DataLoader].
 *
 * For example:
 * * A SQL data loader loads from an outcome of a SQL SELECT, via JDBC's [java.sql.ResultSet]. The data row
 *   in this case is a single row in the SQL SELECT which is a collection of database columns. The properties are
 *   for example a database table column name (`PERSON_NAME`), or a column reference (`p.person_name` in
 *   `select p.person_name from Person p`.
 * * For a REST data loader this follows the REST endpoint naming scheme of the JSON maps returned via the GET.
 *   For example if the REST returns `[{"person_name": "John"}]`,
 *   then the row contains the property of `person_name`.
 */
typealias NativePropertyName = String

/**
 * A name of a single property that the [DataLoader] accepts as a filter property name, or a sort clause property name.
 * Every [DataLoader] should support Java Bean Property names; in addition to that it may
 * additionally accept [NativePropertyName] as [DataLoaderPropertyName], to reference native properties in filters and
 * sort clauses. Every [DataLoader] must document what exactly he accepts (and what he doesn't accept).
 *
 * The data loader takes a data row and converts it into a Java Bean. It needs to map the data row native property names
 * and maps them to
 * Typically a data row is a collection of properties:
 *
 * * Java Beans have a collection of Java Bean Properties: having `getFoo()` and optionally `setFoo()` forms a Java Bean Property.
 * Java Bean property is defined in the [java.beans.PropertyDescriptor] API.
 * * Kotlin classes are collections of [kotlin.reflect.KProperty] (which are also Java Bean Properties).
 * * A database record is a collection of values, one for every database table column.
 * * A JSON transferred over a REST is typically a JSON map, mapping property names to property values.
 *
 * Typically all of the above data sources transfer the data rows into Java Beans, and therefore
 * it is strongly advised that the property name is almost always the Java Bean Property name. For example:
 *
 * * [vok-orm](https://github.com/mvysny/vok-orm) maps select outcome to Java/Kotlin class, therefore we could use Java Bean
 * Property names as the data row property name, however there is a catch:
 *     * The SQL databases can not create WHERE
 *       clauses based on aliases: [vok-orm issue 5](https://github.com/mvysny/vok-orm/issues/5). Therefore, we need to
 *       eventually transform the property name into the column name when constructing SQL SELECT queries. We can achieve that
 *       using an `@As` annotation on the Java Bean Property.
 * * [vok-rest-client](https://github.com/mvysny/vaadin-on-kotlin/tree/master/vok-rest-client) uses Gson to turn list of
 * JSON maps into a list of Java Beans. It is a good practice for the REST filter names and the sorting criteria property naming
 * to follow names of keys in the JSON maps, and hence we will most probably have a Java Bean Property for every filter
 * or sort clause we can have.
 *   * REST endpoints may decide to use the `lowercase_underscore` naming scheme; however that is the responsibility of
 *     the REST client (e.g. to configure Gson to use column name aliases, for example using the `@As` annotation).
 *
 * But what about unmapped properties? Surely we may need to sort SELECTs based on columns that aren't finally returned.
 * It is therefore the responsibility of the [DataLoader] to precisely define what exactly the property name is, without any doubt.
 * For example, a SQL database-backed [DataLoader] will support Java Bean Property names but also SQL column names or referrals
 * or any SQL expression that can go into the WHERE/ORDER BY clause.
 */
typealias DataLoaderPropertyName = String
