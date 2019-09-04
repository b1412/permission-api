# Kotlin Cannon
[![Build Status](https://travis-ci.org/b1412/kotlin-cannon.svg?branch=master)](https://travis-ci.org/b1412/kotlin-cannon)[![codecov](https://codecov.io/gh/b1412/kotlin-cannon/branch/master/graph/badge.svg)](https://codecov.io/gh/b1412/kotlin-cannon)

## Quick start
  build app and image 
```shell 
scripts/build.sh
```
   start mysql & app
```shell
docker-compose up
```

open your browser

http://localhost:8080/graphiql

write your first GraphQL query

```graphql
{
  Branch {
    id
    name
    number
    users {
      id
      email
      login
      role {
        id
        name
      }
    }
  }
}
```
see your result
![data](https://raw.githubusercontent.com/b1412/cannon/master/images/graphql.png)
