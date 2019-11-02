# Kotysa for Spring data R2DBC

## Dependency

Kotysa is a single dependency to add to your java 8+ project.

```groovy
repositories {
    maven { url 'https://dl.bintray.com/pull-vert/kotysa' }
}

dependencies {
    implementation 'com.pullvert:kotysa-r2dbc:0.0.11'
}
```

## Reactive support

**SqlClient** has one reactive implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient```, it can be obtained via an Extension function directly on ```DatabaseClient```. It provides an API using Reactor ```Mono``` and ```Flux```.

```kotlin
class UserRepository(dbClient: DatabaseClient, tables: Tables) {

	private val sqlClient = dbClient.sqlClient(tables)

	// enjoy sqlClient with Reactor :)
}
```

## Coroutines first class support

**SqlClient** has one Coroutines implementation on top of R2DBC using spring-data-r2dbc's ```DatabaseClient```, it can be obtained via an Extension function directly on ```DatabaseClient```. It provides an API using ```suspend``` functions and kotlinx-coroutines ```Flow```.

```kotlin
class UserRepository(dbClient: DatabaseClient, tables: Tables) {

	private val sqlClient = dbClient.coSqlClient(tables)

	// enjoy sqlClient use with coroutines :)
}
```
