[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/pull-vert/kotysa/kotysa/images/download.svg) ](https://bintray.com/pull-vert/kotysa/kotysa/_latestVersion)

Kotysa
==================

Kotysa (**Ko**tlin **Ty**pe-**Sa**fe) is a [Sql client](kotysa-core/src/main/kotlin/com/pullvert/kotysa/SqlClient.kt). You write type-safe database queries, Kotysa generates SQL for you.

```kotlin
sqlClient.apply {
    createTable<User>()
    deleteFromTable<User>() // delete All users
    insert(jdoe, bboss)
    val john = select<User>()
            .where { it[User::firstname] eq "John" }
            .fetchFirst()
}
```

It is agnostic from chosen Sql Engine, written in Kotlin for Kotlin users.

Type-safety relies on Entity property's (or getter's) type and nullability. It is used to allow [available column SQL type(s)](#data-types), select typed fields and allow only limited list of WHERE operations depending on type.

## Dependency

Kotysa is a single dependency to add to your java 8+ project.

```groovy
repositories {
    maven { url 'https://dl.bintray.com/pull-vert/kotysa' }
}

dependencies {
    implementation 'com.pullvert:kotysa-r2dbc:0.0.x' // more modules to come soon
}
```

## Code samples

### Describe Database Model with Type-Safe DSL

```tables``` functional DSL is used to define all mapped tables' structure, by linking every table to a class (aka Entity).

```kotlin
val tables =
        tables().h2 { // choose database type
            table<User> {
                name = "users"
                column { it[User::login].varchar().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }
                column { it[User::isAdmin].boolean() }
                column { it[User::alias].varchar() }
            }
        }

data class User(
        val login: String,
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val alias: String? = null
)
```

### Write Type-Safe Queries with SqlClient

**SqlClient** supports :
* ```select<T>``` that returns one (```fetchOne()```) or several (```fetchAll()```) results
* ```createTable<T>``` and ```createTables``` for table creation
* ```insert``` for single or multiple rows insertion
* ```deleteFromTable<T>``` that returns number of deleted rows

```kotlin
fun createTable() = sqlClient.createTable<User>()

fun insert() = sqlClient.insert(jdoe, bboss)

fun deleteAll() = sqlClient.deleteFromTable<User>().execute()

fun findAll() = sqlClient.select<User>().fetchAll()

fun findAllMappedToDto() =
        sqlClient.select {
            UserDto("${it[User::firstname]} ${it[User::lastname]}",
                    it[User::alias])
        }.fetchAll()
        
fun findFirstByFirstname(firstname: String) = sqlClient.select<User>()
        .where { it[User::firstname] eq firstname }
        // null String forbidden        ^^^^^^^^^
        .fetchFirst()

fun findAllByAlias(alias: String?) = sqlClient.select<User>()
        .where { it[User::alias] eq alias }
        // null String accepted     ^^^^^ , if alias==null, gives "WHERE user.alias IS NULL"
        .fetchAll()

val jdoe = User("jdoe", "John", "Doe", false)
val bboss = User("bboss", "Big", "Boss", true, "TheBoss")

data class UserDto(
		val name: String,
		val alias: String?
)
```

### Use SqlClient with R2dbc

**SqlClient** has one Reactive (using Reactor ```Mono``` and ```Flux```) implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient``` : [SqlClientR2dbc](kotysa-r2dbc/src/main/kotlin/com/pullvert/kotysa/r2dbc/SqlClientR2dbc.kt), it can be obtained via an Extension function directly on ```DatabaseClient``` :
```kotlin
fun DatabaseClient.sqlClient(tables: Tables) : ReactorSqlClient
```

```kotlin
class UserRepository(dbClient: DatabaseClient) {

	private val sqlClient = dbClient.sqlClient(tables)

	// enjoy sqlClient use :)
}
```

**SqlClient** blocking version for JDBC has no implementation for now.

## Data types

Supported data types will be updated as new types are added. Java 8+ ```java.time.*``` date types are used.

### H2

<table>
    <tr>
        <th>Kotlin type</th>
        <th>Description
        <th>H2 SQL type</th>
    </tr>
    <tr>
        <td>String</td>
        <td>Variable-length character string, maximum length fixed</td>
        <td>VARCHAR</td>
    </tr>
    <tr>
        <td>java.time.LocalDate</td>
        <td>Represents a date without time part and without timezone</td>
        <td>DATE</td>
    </tr>
    <tr>
        <td rowspan="2">java.time.LocalDateTime</td>
        <td rowspan="2">Represents a date+time without timezone</td>
        <td>TIMESTAMP</td>
    </tr>
    <tr>
        <td>DATETIME</td>
    </tr>
    <tr>
        <td>java.time.Instant</td>
        <td>Represents a date+time with timezone</td>
        <td>TIMESTAMP WITH TIME ZONE</td>
    </tr>
    <tr>
        <td>java.time.LocalTime</td>
        <td>Represents a time without a date part and without timezone</td>
        <td>TIME(9)</td>
    </tr>
    <tr>
        <td>Boolean</td>
        <td>nullable Boolean is not allowed ! Representing a boolean state</td>
        <td>BOOLEAN</td>
    </tr>
</table>
