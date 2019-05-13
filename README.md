# kotysa

Kotysa (**Ko**tlin **Ty**pe-**Sa**fe) is a [SqlClient](src/main/kotlin/com/pullvert/kotysa/SqlClient.kt) agnostic from your chosen Sql Engine, written in Kotlin for Kotlin.

```tables``` functional DSL is used to declare all mapped tables and link functionally each table to an existing class (aka Entity).

**SqlClient** has one Reactive implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient``` : [SqlClientR2dbc](src/main/kotlin/com/pullvert/kotysa/r2dbc/SqlClientR2dbc.kt), it can be obtained via an Extension function directly on ```DatabaseClient``` :
```kotlin
fun DatabaseClient.sqlClient(tables: Tables) : ReactorSqlClient
```

At this point it supports :
* ```select<T>``` that can return one (```Mono<T>```) or several (```Flux<T>```) results.
* table creation with ```createTable<T>``` and ```createTables```
* ```insert``` for single or multiple rows insertion

Type-safety is based on Entity property's type and nullability. It is used to provide available column type, select fields and where clauses (soon).

**SqlClient** blocking version for JDBC has no implementation for now.

## Code examples
### R2dbc

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

class TestRepository(dbClient: DatabaseClient) {

	private val sqlClient = dbClient.sqlClient(tables)

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
}

val jdoe = User("jdoe", "John", "Doe")
val bboss = User("bboss", "Big", "Boss", "TheBoss")

data class User(
		val login: String,
		val firstname: String,
		val lastname: String,
		val alias: String? = null
)

data class UserDto(
		val name: String,
		val alias: String?
)
```
