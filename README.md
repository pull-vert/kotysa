[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/pull-vert/kotysa/kotysa/images/download.svg) ](https://bintray.com/pull-vert/kotysa/kotysa/_latestVersion)

Kotysa
==================

Kotysa (**Ko**tlin **Ty**pe-**Sa**fe) is a [Sql client](kotysa-core/src/main/kotlin/com/pullvert/kotysa/SqlClient.kt) that help you write type-safe database queries, agnostic from chosen Sql Engine, written in Kotlin for Kotlin users.

Type-safety relies on Entity property's (or getter's) type and nullability. It is used to allow available column's SQL type, select typed fields and allow only limited list of WHERE operations depending on type.

## Dependency

Kotysa is a single dependency to add to your project.

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

```tables``` functional DSL is used to define all mapped tables' structure, and link each DB table to an existing class (aka Entity).

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
        // null String accepted     ^^^^^ , if alias=null, gives "WHERE user.alias IS NULL"
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
