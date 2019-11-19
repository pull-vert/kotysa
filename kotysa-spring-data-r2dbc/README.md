# Kotysa for Spring data R2DBC

## Dependency

Kotysa is an additional dependency you can add to your Spring project.
It is an extension to Spring Data R2DBC, and does not replace it.

```groovy
repositories {
    maven { url 'https://dl.bintray.com/pull-vert/kotysa' }
}

dependencies {
    implementation 'com.pullvert:kotysa-spring-data-r2dbc:0.0.12'
    
    implementation "org.springframework.data:spring-data-r2dbc:$spring_data_r2dbc_version"
}
```

## Reactive support

**SqlClient** has one reactive implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient```, it can be obtained via an Extension function directly on ```DatabaseClient```.

It provides a SQL client API using Reactor ```Mono``` and ```Flux```.

```kotlin
class UserRepository(dbClient: DatabaseClient, tables: Tables) {

	private val sqlClient = dbClient.sqlClient(tables)

	// enjoy sqlClient with Reactor :)
}
```

## Coroutines first class support

**SqlClient** has one Coroutines implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient```, it can be obtained via an Extension function directly on ```DatabaseClient```.

It provides a SQL client API using ```suspend``` functions and kotlinx-coroutines ```Flow```.

```kotlin
class UserRepository(dbClient: DatabaseClient, tables: Tables) {

	private val sqlClient = dbClient.coSqlClient(tables)

	// enjoy sqlClient use with coroutines :)
}
```

## Supported databases

* [H2](../docs/table-modelling.md#H2)
