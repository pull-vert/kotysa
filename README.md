[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/pull-vert/kotysa/kotysa/images/download.svg) ](https://bintray.com/pull-vert/kotysa/kotysa/_latestVersion)

# Kotysa

Kotysa (**Ko**tlin **Ty**pe-**Sa**fe) Sql client is a type-safe object mapping and SQL generator.

```kotlin
sqlClient.apply {
    createTable<User>()
    deleteAllFromTable<User>()
    insert(jdoe, bboss)
    
    val john = select<User>()
            .where { it[User::firstname] eq "John" }
            .fetchFirst()
}
```

**No annotations, no code generation, just regular Kotlin code ! No JPA, just pure SQL !**

Kotysa is agnostic from Sql Engine, written in Kotlin for Kotlin users.

[Available column SQL type(s)](#data-types), typed fields in SELECT and available WHERE operations depends on Entity properties' (or getters') type and nullability.

* Create Kotlin entities, data classes are great for that
* [Describe Database Model with Type-Safe DSL](#describe-database-model-with-type-safe-dsl) based on these entities
* [Write Type-Safe Queries with SqlClient](#write-type-safe-queries-with-sqlclient), Kotysa generates SQL for you !

Kotysa provide [Coroutines first class support](#coroutines-first-class-support)

Kotysa is **not production ready yet**, some key features are still missing. Early releases will continue to be provided with new features.

## Dependency

Kotysa is a single dependency to add to your java 8+ project.

```groovy
repositories {
    maven { url 'https://dl.bintray.com/pull-vert/kotysa' }
}

dependencies {
    implementation 'com.pullvert:kotysa-r2dbc:0.0.x'
}
```

more Kotysa modules will come soon

## Code samples

### Describe Database Model with Type-Safe DSL

```tables``` functional DSL is used to define all mapped tables' structure, by linking every table to a class (aka Entity).

```kotlin
val tables =
        tables {
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
* ```select<T>``` that returns one (```fetchOne()``` and ```fetchFirst()```) or several (```fetchAll()```) results
* ```createTable<T>``` for table creation
* ```insert``` for single or multiple rows insertion
* ```deleteFromTable<T>``` that returns number of deleted rows
* ```updateTable<T>``` to update fields

```kotlin
fun createTable() = sqlClient.createTable<User>()

fun insert() = sqlClient.insert(jdoe, bboss)

fun deleteAll() = sqlClient.deleteAllFromTable<User>()

fun deleteById(id: String) = sqlClient.deleteFromTable<User>()
        .where { it[User::login] eq id }
        .execute()

fun selectAll() = sqlClient.selectAll<User>()

fun countAll() = sqlClient.countAll<User>()

fun countWithAlias() = sqlClient.select { count { it[User::alias] } }.fetchOne()

fun selectAllMappedToDto() =
        sqlClient.select {
            UserDto("${it[User::firstname]} ${it[User::lastname]}",
                    it[User::alias])
        }.fetchAll()
        
fun selectFirstByFirstname(firstname: String) = sqlClient.select<User>()
        .where { it[User::firstname] eq firstname }
        // null String forbidden        ^^^^^^^^^
        .fetchFirst()

fun selectAllByAlias(alias: String?) = sqlClient.select<User>()
        .where { it[User::alias] eq alias }
        // null String accepted     ^^^^^ , if alias==null, gives "WHERE user.alias IS NULL"
        .fetchAll()
        
fun updateFirstname(newFirstname: String) = sqlClient.updateTable<User>()
        .set { it[User::firstname] = newFirstname }
        .execute()

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

### Coroutines first class support

**SqlClient** has one Coroutines (using ```suspend``` and kotlinx-coroutines ```Flow```) implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient``` : [CoroutineSqlClientR2dbc](kotysa-r2dbc/src/main/kotlin/com/pullvert/kotysa/r2dbc/CoroutinesSqlClientR2dbc.kt), it can be obtained via an Extension function directly on ```DatabaseClient``` :
```kotlin
fun DatabaseClient.coSqlClient(tables: Tables): CoroutinesSqlClientR2dbc
```

```kotlin
class UserRepository(dbClient: DatabaseClient) {

	private val sqlClient = dbClient.coSqlClient(tables)

	// enjoy sqlClient use with coroutines :)
}
```

**SqlClient** blocking version has no implementation for now.

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
