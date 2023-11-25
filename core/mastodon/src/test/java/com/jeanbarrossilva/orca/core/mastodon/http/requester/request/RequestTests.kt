package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database.RequestEntity
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.http.parametersOf
import kotlin.test.Test

internal class RequestTests {
  @Test
  fun convertsGetRequestIntoEntity() {
    assertThat(
        Request.Get(
            route = "api/v1",
            parametersOf("id", "123"),
            headersOf(HttpHeaders.Authorization, "Bearer 1234")
          )
          .toRequestEntity()
      )
      .isEqualTo(
        RequestEntity(
          Request.Get.METHOD_NAME,
          route = "api/v1",
          "Parameters [id=[123]]",
          "Headers [${HttpHeaders.Authorization}=[Bearer 1234]]"
        )
      )
  }

  @Test
  fun convertsPostRequestIntoEntity() {
    assertThat(
        Request.Post(
            route = "api/v1",
            parametersOf("id", "123"),
            headersOf(HttpHeaders.Authorization, "Bearer 1234")
          )
          .toRequestEntity()
      )
      .isEqualTo(
        RequestEntity(
          Request.Post.METHOD_NAME,
          route = "api/v1",
          "Parameters [id=[123]]",
          "Headers [${HttpHeaders.Authorization}=[Bearer 1234]]"
        )
      )
  }
}
