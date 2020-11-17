# Kotlin Cannon
![Permission API CI](https://github.com/b1412/permission-api/workflows/Permission%20API%20CI/badge.svg)

## Start from DockerHub

https://hub.docker.com/r/b1412/kotlin-cannon

## Quick source code
  build app and image 
```shell 
scripts/build.sh
```
   start mysql & app
```shell
docker-compose up
```
**NOTE** `db/dump.sql` only be executed once the first time you start the container.

open your browser

http://localhost:8080/graphiql

write your first GraphQL query

```graphql
{
  Branch {
    totalPages
    totalElements
    content {
      id
      name
      users {
        id
        email
        role {
          name
        }
      }
      active
    }
  }
}


{
  User(where: {email_like: "e", username_like: "l"}, pageRequest: {size: 5, page: 1}) {
    totalPages
    totalElements
    content {
      id
      login
      email
      role {
        id
        name
      }
    }
  }
}
```

##References

https://github.com/jcrygier/graphql-jpa
