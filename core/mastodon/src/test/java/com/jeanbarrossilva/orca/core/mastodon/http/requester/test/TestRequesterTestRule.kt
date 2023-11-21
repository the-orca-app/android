package com.jeanbarrossilva.orca.core.mastodon.http.requester.test

import com.jeanbarrossilva.orca.core.mastodon.http.requester.Requester
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import org.junit.rules.ExternalResource

/**
 * Resets the [requester] at the end of each test.
 *
 * @param initialRequester Initial [TestRequester].
 */
internal class TestRequesterTestRule(
  private val initialRequester: SomeTestRequester = TestRequester()
) : ExternalResource() {
  var requester = initialRequester
    private set

  public override fun after() {
    requester = initialRequester
    Requester.clear()
  }

  /**
   * Changes the [CoroutineContext] in which requests are performed in the [requester]'s
   * [coroutineScope][TestRequester.coroutineScope].
   *
   * @param context [CoroutineContext] to run future operations in.
   */
  fun on(context: CoroutineContext): TestRequesterTestRule {
    return apply {
      @Suppress("DEPRECATION")
      requester = requester.on(context)
    }
  }

  /**
   * Makes the [requester]'s [client][TestRequester.client] respond with the result of the given
   * [response] when a request is sent through it.
   *
   * @param response Returns the response that will be provided for future requests.
   */
  fun respond(
    response: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData
  ): TestRequesterTestRule {
    return apply {
      @Suppress("DEPRECATION")
      requester = requester.respond(response)
    }
  }

  /**
   * Changes the [requester] to a version of it that delays all of its requests.
   *
   * @param delay [Duration] of the delay.
   */
  fun delayedBy(delay: Duration): TestRequesterTestRule {
    return apply {
      @Suppress("DEPRECATION")
      requester = requester.delayedBy(delay)
    }
  }
}
