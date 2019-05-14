[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)

Kotysa
==================

Kotysa (**Ko**tlin **Ty**pe-**Sa**fe) is a [SqlClient](src/main/kotlin/com/pullvert/kotysa/SqlClient.kt), agnostic from your chosen Sql Engine, written in Kotlin for Kotlin users.

```tables``` functional DSL is used to declare all mapped tables and link each DB table to an existing class (aka Entity).

At this point it supports :
* ```select<T>``` that can return one (```Mono<T>```) or several (```Flux<T>```) results.
* table creation with ```createTable<T>``` and ```createTables```
* ```insert``` for single or multiple rows insertion

Type-safety is based on Entity property's type and nullability. It is used to provide available column type, select fields and where clauses (soon).

**SqlClient** has one Reactive implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient``` : [SqlClientR2dbc](src/main/kotlin/com/pullvert/kotysa/r2dbc/SqlClientR2dbc.kt), it can be obtained via an Extension function directly on ```DatabaseClient``` :
```kotlin
fun DatabaseClient.sqlClient(tables: Tables) : ReactorSqlClient
```

**SqlClient** blocking version for JDBC has no implementation for now.

## Code samples
### Describe Database Model with Type-Safe DSL
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
```kotlin
fun init() {
	sqlClient.createTables()
			.then(client.insert(jdoe, bboss))
			.block()
	}

fun findAll() =	sqlClient.select<User>().fetchAll()

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
### R2dbc Configuration
```kotlin
class TestRepository(dbClient: DatabaseClient) {

	private val sqlClient = dbClient.sqlClient(tables)

	// enjoy sqlClient use :)
}
```
