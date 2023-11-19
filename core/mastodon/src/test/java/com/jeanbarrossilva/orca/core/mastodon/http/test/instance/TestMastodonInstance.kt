package com.jeanbarrossilva.orca.core.mastodon.http.test.instance

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.instance.Instance
import com.jeanbarrossilva.orca.core.mastodon.http.CoreHttpClient
import com.jeanbarrossilva.orca.core.mastodon.http.Logger
import com.jeanbarrossilva.orca.core.mastodon.instance.MastodonInstance
import com.jeanbarrossilva.orca.core.sample.test.instance.sample
import com.jeanbarrossilva.orca.core.test.TestAuthenticationLock
import com.jeanbarrossilva.orca.core.test.TestAuthenticator
import com.jeanbarrossilva.orca.core.test.TestAuthorizer
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockEngineConfig
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respondOk
import io.ktor.client.request.HttpRequest
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData

/**
 * [MastodonInstance] whose [client] responds OK to each sent [HttpRequest].
 *
 * @param authorizer [TestAuthorizer] with which the user will be authorized.
 * @param respond Responds to an [HttpRequest].
 */
internal class TestMastodonInstance(
  authorizer: TestAuthorizer = TestAuthorizer(),
  override val authenticator: TestAuthenticator = TestAuthenticator(authorizer),
  override val authenticationLock: AuthenticationLock<TestAuthenticator> =
    TestAuthenticationLock(authenticator = authenticator),
  private val respond: MockRequestHandleScope.(HttpRequestData) -> HttpResponseData = {
    respondOk()
  }
) : MastodonInstance<TestAuthorizer, TestAuthenticator>(Instance.sample.domain, authorizer) {
  /**
   * [HttpClientEngineFactory] that creates a [MockEngine] that sends an OK response to each
   * [HttpRequest].
   */
  private val clientEngineFactory =
    object : HttpClientEngineFactory<MockEngineConfig> {
      override fun create(block: MockEngineConfig.() -> Unit): HttpClientEngine {
        return MockEngine(respond).apply { block(config) }
      }
    }

  override val feedProvider = Instance.sample.feedProvider
  override val profileProvider = Instance.sample.profileProvider
  override val profileSearcher = Instance.sample.profileSearcher
  override val tootProvider = Instance.sample.tootProvider
  override val client = CoreHttpClient(clientEngineFactory, Logger.test)
}
