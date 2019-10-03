[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/pull-vert/kotysa/kotysa/images/download.svg) ](https://bintray.com/pull-vert/kotysa/kotysa/_latestVersion)

# Kotysa

Kotysa Sql client is a **Ko**tlin **ty**pe-**sa**fe functional object mapping and SQL generator.

```kotlin
sqlClient.apply {
    createTable<User>()
    deleteAllFromTable<User>()
    insert(jdoe, bboss)
    
    val count = countAll<User>()
    
    val all = selectAll<User>()
    
    val johny = select { UserWithRoleDto(it[User::lastname], it[Role::label]) }
            .innerJoinOn<Role> { it[User::roleId] }
            .where { it[User::alias] eq "Johny" }
            .fetchFirst()
}
```

**No annotations, no code generation, just regular Kotlin code ! No JPA, just pure SQL !**

Kotysa is agnostic from Sql Engine, written in Kotlin for Kotlin users.

Type safety relies on type and nullability of the Entity property (or getter).

Easy to use :
* Create Kotlin entities : data classes are great for that !
* [Describe database model with type-safe DSL](#describe-database-model-with-type-safe-dsl) based on these entities
* [Write type-safe queries with SqlClient DSL](#write-type-safe-queries-with-sqlclient), Kotysa generates SQL for you !

Kotysa provides [Coroutines first class support](#coroutines-first-class-support)

[Check Kotysa available column SQL types](#data-types)

Kotysa is **not production ready yet**, some key features are still missing. Early early release will provide new features.

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
data class User(
        val firstname: String,
        val lastname: String,
        val isAdmin: Boolean,
        val roleId: UUID,
        val alias: String? = null,
        val id: UUID = UUID.randomUUID()
)

data class Role(
        val label: String,
        val id: UUID = UUID.randomUUID()
)

val tables =
        tables().h2 { // choose database type
            table<Role> {
                name = "roles"
                column { it[Role::id].uuid().primaryKey }
                column { it[Role::label].varchar() }
            }
            table<User> {
                name = "users"
                column { it[User::id].uuid().primaryKey }
                column { it[User::firstname].varchar().name("fname") }
                column { it[User::lastname].varchar().name("lname") }                
                column { it[User::isAdmin].boolean() }
                column { it[User::roleId].uuid().foreignKey<Role>() }
                column { it[User::alias].varchar() }
            }
        }
```

### Write Type-Safe Queries with SqlClient

**SqlClient** supports :
* ```select<T>``` that returns one (```fetchOne()``` and ```fetchFirst()```) or several (```fetchAll()```) results
* ```createTable<T>``` for table creation
* ```insert``` for single or multiple rows insertion
* ```deleteFromTable<T>``` that returns number of deleted rows
* ```updateTable<T>``` to update fields, returns number of updated rows

```kotlin
fun createTable() = sqlClient.createTable<User>()

fun insert() = sqlClient.insert(jdoe, bboss)

fun deleteAll() = sqlClient.deleteAllFromTable<User>()

fun deleteById(id: UUID) = sqlClient.deleteFromTable<User>()
        .where { it[User::id] eq id }
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

val jdoe = User("John", "Doe", false)
val bboss = User("Big", "Boss", true, "TheBoss")

data class UserDto(
		val name: String,
		val alias: String?
)
```

### Use SqlClient with R2dbc

**SqlClient** has one reactive implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient```, it can be obtained via an Extension function directly on ```DatabaseClient```. It provide an API using Reactor ```Mono``` and ```Flux```.

```kotlin
class UserRepository(dbClient: DatabaseClient) {

	private val sqlClient = dbClient.sqlClient(tables)

	// enjoy sqlClient use :)
}
```

### Coroutines first class support

**SqlClient** has one Coroutines implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient```, it can be obtained via an Extension function directly on ```DatabaseClient```. It provide an API using ```suspend``` functions and kotlinx-coroutines ```Flow```.

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
        <td>java.time.OffsetDateTime</td>
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
    <tr>
        <td>java.util.UUID</td>
        <td>Universally unique identifier (128 bit value)</td>
        <td>UUID</td>
    </tr>
</table>
