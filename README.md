[![Powered By Vaadin on Kotlin](http://vaadinonkotlin.eu/iconography/vok_badge.svg)](http://vaadinonkotlin.eu)
[![Build Status](https://travis-ci.org/mvysny/vok-dataloader.svg?branch=master)](https://travis-ci.org/mvysny/vok-dataloader)

# VOK DataLoaders

A simple API tailored to fetch data from various backend systems, via
variety of APIs, so that the data can be shown in a table UI component, for example
in a Vaadin Grid.

This is just an API:

* See [vok-orm](https://github.com/mvysny/vok-orm) for a data loader loading
  entities from a SQL database;
* See [vok-rest-client](https://github.com/mvysny/vaadin-on-kotlin/tree/master/vok-rest-client)
  for a data loader loading entities from a REST server.

## About the Data Loader

The [DataLoader](src/main/kotlin/com/github/mvysny/vokdataloader/DataLoader.kt] is but a very simple interface:

```kotlin
interface DataLoader<T: Any> : Serializable {
    fun getCount(filter: Filter<T>?): Long
    fun fetch(filter: Filter<T>?, sortBy: List<SortClause>, range: LongRange): List<T>
}
```

It accesses a pageable/filtrable/sortable native data set of some sort. For example, a SQL data loader will run SELECT, take
the JDBC ResultSet and convert every row into a Java Bean. Another example would be a REST endpoint data loader which
takes a list of JSON maps and converts every row (=JSON map) into a Java Bean.

The native data set is expected to contain zero, one or more native data rows. The data loader retrieves native data rows
and turns them into instances of bean of some type (one data row into one bean).

## Data Loader Properties

Both filters and sort clauses accept the property names upon which the sorting
or filtering should occur. However, there is a very important distinction to make,
between _native_ properties and _dataloader_ properties.

### Native Properties

Native properties are named according to the naming of the native raw data row as loaded by the DataLoader.

For example:

* A SQL data loader loads from an outcome of a SQL SELECT, via JDBC's `ResultSet`. The data row
  in this case is a single row in the SQL SELECT which is a collection of database columns. The native property names are
  for example a database table column name (`PERSON_NAME`), or a column reference (`p.person_name` in
  `select p.person_name from Person p`).
* For a REST data loader this follows the REST endpoint naming scheme of the JSON maps returned via the GET.
  For example if the REST returns `[{"person_name": "John"}]`,
  then the row contains the property of `person_name`.
* For an in-memory collection of Java Beans, the native property name is already the Java Bean Property name, and
  therefore there is no distinction to `DataLoaderPropertyName` in this case.

### DataLoaderPropertyName

A name of a single property that the `DataLoader` accepts as a filter property name, or a sort clause property name.
Since every data loader produces Java Beans, every `DataLoader` MUST support Java Bean Property names
as `DataLoaderPropertyName`. In addition to that, the DataLoader MAY decide to
additionally accept `NativePropertyName`s as `DataLoaderPropertyName`s, to allow the user to reference native properties in filters and
sort clauses which aren't mapped to the Java Bean.
Every `DataLoader` MUST document what exactly he accepts (and what he doesn't accept).

For example:

* [vok-orm](https://github.com/mvysny/vok-orm) maps SQL SELECT outcome to Java/Kotlin class. It needs to map
`DataLoaderPropertyName` to `NativePropertyName` manually (can't use SQL aliases to map `DataLoaderPropertyName` to `NativePropertyName`
e.g. `select p.person_name as personName`) since SQL databases can not create WHERE
clauses based on aliases: [vok-orm issue 5](https://github.com/mvysny/vok-orm/issues/5). `vok-orm` is therefore
using the `@As` annotation on the Java Bean Property. In addition however we must support sorting and filtering based on
`NativePropertyName` to allow filtering on columns not returned/mapped to Java Bean; in this case the native property name
is for example a database table column name (`PERSON_NAME`), or a column reference (`p.person_name` in
  `select p.person_name from Person p`).
* [vok-rest-client](https://github.com/mvysny/vaadin-on-kotlin/tree/master/vok-rest-client) uses Gson to turn list of
JSON maps into a list of Java Beans. It is a good practice for the REST filter names and the sorting criteria property naming
to follow names of keys in the JSON maps, and hence we will most probably have a Java Bean Property for every filter
or sort clause we can have. REST endpoints may decide to use the `lowercase_underscore` (or any other) naming scheme;
it is therefore the responsibility of REST data loader to e.g. to configure Gson to use column name aliases, for example using the `@As` annotation.
