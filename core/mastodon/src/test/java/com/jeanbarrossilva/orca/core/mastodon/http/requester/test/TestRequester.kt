package com.jeanbarrossilva.orca.core.mastodon.http.requester.test

import com.jeanbarrossilva.orca.core.mastodon.http.client.CoreHttpClient
import com.jeanbarrossilva.orca.core.mastodon.http.client.Logger
import com.jeanbarrossilva.orca.core.mastodon.http.requester.Requester
import com.jeanbarrossilva.orca.core.mastodon.instance.test.test
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.client.statement.HttpResponse
import io.ktor.http.content.PartData
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.plus

/** [TestRequester] with a generic type. */
internal typealias SomeTestRequester = TestRequester<*>

/**
 * [Requester] that has a pre-configured [client] whose responses can be changed.
 *
 * Its factory methods, such as [on], [respond] and [delayedBy], shouldn't be called directly if not
 * by a [TestRequesterTestRule].
 *
 * @param T Type of the subclass that extends this [TestRequester].
 * @see respond
 */
internal open class TestRequester<T : TestRequester<T>> : Requester() {
  /** Defines what the [client] receives as a response to a request. */
  private var response: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
    respondBadRequest()
  }

  public override val retained = super.retained

  public final override var coroutineScope = super.coroutineScope
    private set

  override val client: HttpClient =
    CoreHttpClient(
      object : HttpClientEngineFactory<MockEngineConfig> {
        override fun create(block: MockEngineConfig.() -> Unit): HttpClientEngine {
          return MockEngine { response(it) }
        }
      },
      Logger.test
    )

  private val delegate by lazy { through(client) }

  override suspend fun onGet(route: String): HttpResponse {
    return delegate.get(route)
  }

  override suspend fun onPost(route: String, form: List<PartData>): HttpResponse {
    return delegate.post(route, form)
  }

  /**
   * Whether the request sent to the given [route] is currently ongoing.
   *
   * @param route Path to which the request whose status will be checked was sent.
   */
  fun isRequestOngoing(route: String): Boolean {
    return route in ongoing
  }

  /**
   * Changes the [CoroutineContext] in which requests are performed within the [coroutineScope].
   *
   * @param context [CoroutineContext] to run future operations in.
   */
  @Deprecated(
    "When referencing a TestRequesterTestRule's TestRequester, the response provided by its " +
      "HttpClient won't have changed because its reference has to be changed by the test rule " +
      "itself.",
    ReplaceWith(
      "TestRequesterTestRule().respond(response)",
      "com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule"
    )
  )
  fun on(context: CoroutineContext): T {
    @Suppress("UNCHECKED_CAST") return apply { coroutineScope += context } as T
  }

  /**
   * Makes the [client] respond with the result of the given [response] when a request is sent
   * through it.
   *
   * @param response Returns the response that will be provided for future requests.
   */
  @Deprecated(
    "When referencing a TestRequesterTestRule's TestRequester, the response provided by its " +
      "HttpClient won't have changed because its reference has to be changed by the test rule " +
      "itself.",
    ReplaceWith(
      "TestRequesterTestRule().respond(response)",
      "com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule"
    )
  )
  fun respond(response: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData): T {
    @Suppress("UNCHECKED_CAST") return apply { this.response = response } as T
  }

  /**
   * Creates a version of this [TestRequester] that delays all of its requests.
   *
   * @param delay [Duration] of the delay.
   */
  @Deprecated(
    "When referencing a TestRequesterTestRule's TestRequester, its requests won't be delayed " +
      "because its reference has to be changed by the test rule itself.",
    ReplaceWith(
      "TestRequesterTestRule().delayedBy(delay)",
      "com.jeanbarrossilva.orca.core.mastodon.http.requester.test.TestRequesterTestRule"
    )
  )
  internal fun delayedBy(delay: Duration): DelayedRequester {
    return DelayedRequester(delegate = this, delay)
  }
}
