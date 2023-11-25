package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.jeanbarrossilva.orca.core.mastodon.http.requester.request.Request
import kotlin.test.Test

internal class MethodNameTests {
  @Test
  fun returnsResultOfGetWhenFolding() {
    assertThat(
        RequestEntity.MethodName.fold(Request.Get.METHOD_NAME, onGet = { 0 }, onPost = { 1 })
      )
      .isEqualTo(0)
  }

  @Test
  fun returnsResultOfPostWhenFolding() {
    assertThat(
        RequestEntity.MethodName.fold(Request.Post.METHOD_NAME, onGet = { 0 }, onPost = { 1 })
      )
      .isEqualTo(1)
  }

  @Test(expected = RequestEntity.MethodName.UnknownException::class)
  fun throwsWhenRequiringUnknownMethodNameToBeKnown() {
    RequestEntity.MethodName.requireToBeKnown("🇬🇧")
  }

  @Test
  fun doesNotThrowWhenRequiringKnownGetMethodNameToBeKnown() {
    RequestEntity.MethodName.requireToBeKnown(Request.Get.METHOD_NAME)
  }

  @Test
  fun doesNotThrowWhenRequiringKnownPostMethodNameToBeKnown() {
    RequestEntity.MethodName.requireToBeKnown(Request.Post.METHOD_NAME)
  }
}
