package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import io.ktor.http.parametersOf
import kotlin.test.Test

internal class RequestEntityTests {
  @Test(expected = RequestEntity.MethodName.UnknownException::class)
  fun throwsWhenConvertingEntityWithUnknownMethodNameIntoRequest() {
    RequestEntity(
      methodName = "🇧🇷",
      route = "api/v1",
      "Parameters [id=[123]]",
      "Headers [${HttpHeaders.Authorization}=[Bearer 1234]]"
    )
  }

  @Test
  fun convertsGetEntityIntoRequest() {
    assertThat(
        RequestEntity(
            Request.Get.METHOD_NAME,
            route = "api/v1",
            "Parameters [id=[123]]",
            "Headers [${HttpHeaders.Authorization}=[Bearer 1234]]"
          )
          .toRequest()
      )
      .isEqualTo(
        Request.Get(
          route = "api/v1",
          parametersOf("id", "123"),
          headersOf(HttpHeaders.Authorization, "Bearer 1234")
        )
      )
  }

  @Test
  fun convertsPostEntityIntoRequest() {
    assertThat(
        RequestEntity(
            Request.Post.METHOD_NAME,
            route = "api/v1",
            "Parameters [id=[123]]",
            "Headers [${HttpHeaders.Authorization}=[Bearer 1234]]"
          )
          .toRequest()
      )
      .isEqualTo(
        Request.Post(
          route = "api/v1",
          parametersOf("id", "123"),
          headersOf(HttpHeaders.Authorization, "Bearer 1234")
        )
      )
  }
}
