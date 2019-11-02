[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/pull-vert/kotysa/kotysa/images/download.svg) ](https://bintray.com/pull-vert/kotysa/kotysa/_latestVersion)

# Kotysa

The idiomatic way of writing **Ko**tlin **ty**pe-**sa**fe SQL.

## Easy to use : 3 steps only
### step 1 -> Create Kotlin entities
data classes are great for that !
```kotlin
data class Role(
        val label: String,
        val id: UUID = UUID.randomUUID()
)

data class User(
        val firstname: String,
        val roleId: UUID,
        val alias: String? = null,
        val id: UUID = UUID.randomUUID()
)
```

### step 2 -> Describe database model with [type-safe DSL](docs/table-modelling.md), based on these entities
```kotlin
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
                column { it[User::roleId].uuid().foreignKey<Role>() }
                column { it[User::alias].varchar() }
            }
        }
```

### step 3 -> Write SQL queries with [type-safe SqlClient DSL](docs/sql-queries.md)
Kotysa generates SQL for you !
```kotlin
// return all admin users
sqlClient.select<User>()
        .innerJoin<Role>().on { it[User::roleId] }
        .where { it[Role::label] eq "admin" }
        .fetchAll()
```

**No annotations, no code generation, just regular Kotlin code ! No JPA, just pure SQL !**

## modules
Kotysa is agnostic from Sql Engine (SqLite on Android, R2DBC, JDBC in the future) :
* use Kotysa with [Spring data R2DBC](kotysa-spring-data-r2dbc/README.md)
* use Kotysa with [SqLite on Android](kotysa-android/README.md)

Kotysa is **not production ready yet**, some key features are still missing. Early releases will provide new features (see [next milestones](https://github.com/pull-vert/kotysa/milestones)).

Type safety relies on type and nullability of the Entity property (or getter).

Kotysa provides [Coroutines first class support with R2DBC](kotysa-spring-data-r2dbc/README.md#coroutines-first-class-support)
