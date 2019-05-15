[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/pull-vert/kotysa/kotysa/images/download.svg) ](https://bintray.com/pull-vert/kotysa/kotysa/_latestVersion)

Kotysa
==================

Kotysa (**Ko**tlin **Ty**pe-**Sa**fe) is a [Sql client](src/main/kotlin/com/pullvert/kotysa/SqlClient.kt) that help you write type-safe database queries, agnostic from chosen Sql Engine, written in Kotlin for Kotlin users.

Type-safety relies on Entity property's type and nullability. It is used to limit available column's SQL type, select typed fields and where clauses (soon).

## Dependency

Kotysa is a single dependency to add to your project.

```groovy
repositories {
    maven { url 'https://dl.bintray.com/pull-vert/kotysa' }
}

dependencies {
    implementation 'com.pullvert:kotysa:0.0.x'
}
```

## Code samples

### Describe Database Model with Type-Safe DSL

```tables``` functional DSL is used to declare all mapped tables and link each DB table to an existing class (aka Entity).

```kotlin
private val tables =
		tables {
			table<User> {
				name = "users"
				column { it[User::login].varchar().primaryKey }
				column { it[User::firstname].varchar().name("fname") }
				column { it[User::lastname].varchar().name("lname") }
				column { it[User::alias].varchar() }
			}
		}
		
data class User(
		val login: String,
		val firstname: String,
		val lastname: String,
		val alias: String? = null
)
```

### Write Type-Safe Queries with SqlClient

At this point **SqlClient** supports :
* ```select<T>``` that returns one (```fetchOne()```) or several (```fetchAll()```) results
* table creation with ```createTable<T>``` and ```createTables```
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

val jdoe = User("jdoe", "John", "Doe")
val bboss = User("bboss", "Big", "Boss", "TheBoss")

data class UserDto(
		val name: String,
		val alias: String?
)
```

### Use SqlClient with R2dbc

**SqlClient** has one Reactive (using Reactor ```Mono``` and ```Flux```) implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient``` : [SqlClientR2dbc](src/main/kotlin/com/pullvert/kotysa/r2dbc/SqlClientR2dbc.kt), it can be obtained via an Extension function directly on ```DatabaseClient``` :
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
