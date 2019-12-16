package com.github.b1412.cannon.jpa

import org.junit.jupiter.api.Test

class QueryBuilderTest {

    @Test
    fun test() {

        val input = """
query {
  Branch(filter: {gender_eq: "MAN"
      title_in: ["My biggest Adventure", "My latest Hobbies"]
      name_like: "Txx"
      age_gte: "10"
      rate_lte: "5"
      }
  ){
    id
    name
    users{
      id
      email
      role{
        id
        name
      }
    }
  }
}
        """.trimIndent()
        val url = QueryBuilder.graphqlPayload(input)
        println(url)
        println(QueryBuilder.queryList(url))
    }

}