package com.jeanbarrossilva.orca.core.mastodon.http.test

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.auth.actor.ActorProvider
import com.jeanbarrossilva.orca.core.mastodon.MastodonCoreModule
import com.jeanbarrossilva.orca.core.mastodon.http.CoreHttpClient
import com.jeanbarrossilva.orca.core.mastodon.http.test.instance.TestHttpInstanceProvider
import com.jeanbarrossilva.orca.core.mastodon.http.test.instance.TestMastodonInstance
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.core.sample.auth.actor.sample
import com.jeanbarrossilva.orca.core.sample.feed.profile.toot.content.SampleTermMuter
import com.jeanbarrossilva.orca.core.test.TestActorProvider
import com.jeanbarrossilva.orca.core.test.TestAuthenticator
import com.jeanbarrossilva.orca.core.test.TestAuthorizer
import com.jeanbarrossilva.orca.std.injector.Injector
import io.ktor.client.HttpClient
import io.ktor.client.request.HttpRequest
import io.ktor.http.HttpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

/**
 * [CoroutineScope] created from the [TestScope] in which the test in running, that houses
 * [CoreHttpClient]-test-related structures.
 *
 * @param T Specified [Actor] for performing the testing.
 * @param delegate [TestScope] that's been launched and will provide [CoroutineScope]-like
 *   functionality to this [CoreHttpClientTestScope].
 * @param client [CoreHttpClient] for executing the intended [HttpRequest]s.
 * @param actor [Actor] used when running the test.
 */
internal class CoreHttpClientTestScope<T : Actor>(
  delegate: TestScope,
  val client: HttpClient,
  val actor: T
) : CoroutineScope by delegate

/**
 * [ActorProvider] that provides only the specified [actor].
 *
 * @param actor [Actor] to be provided unconditionally.
 */
internal class FixedActorProvider(private val actor: Actor) : TestActorProvider() {
  override suspend fun remember(actor: Actor) {}

  override suspend fun retrieve(): Actor {
    return actor
  }
}

/**
 * Configures an environment for a [CoreHttpClient] test with an
 * [authenticated][Actor.Authenticated] [Actor], providing the proper [CoreHttpClientTestScope].
 *
 * @param body Callback run when the environment has been set up and is, therefore, ready to be
 *   used.
 */
internal fun runAuthenticatedTest(
  body: suspend CoreHttpClientTestScope<Actor.Authenticated>.() -> Unit
) {
  runCoreHttpClientTest(Actor.Authenticated.sample, onAuthentication = {}, body)
}

/**
 * Configures an environment for a [CoreHttpClient] test with an
 * [unauthenticated][Actor.Unauthenticated] [Actor], providing the proper [CoreHttpClientTestScope].
 *
 * @param onAuthentication Action run whenever the [Actor] is authenticated.
 * @param body Callback run when the environment has been set up and is, therefore, ready to be
 *   used.
 */
internal fun runUnauthenticatedTest(
  onAuthentication: () -> Unit,
  body: suspend CoreHttpClientTestScope<Actor.Unauthenticated>.() -> Unit
) {
  runCoreHttpClientTest(Actor.Unauthenticated, onAuthentication, body)
}

/**
 * Configures an environment for a [CoreHttpClient] test, providing the proper
 * [CoreHttpClientTestScope].
 *
 * @param T [Actor] to run the test with.
 * @param actor [Actor] to be fixedly provided by the underlying [ActorProvider]. Determines the
 *   behavior of `authenticateAnd*` calls done on the [CoreHttpClient] within the [body], given that
 *   an [unauthenticated][Actor.Unauthenticated] [Actor] will be requested to be authenticated when
 *   such invocations take place, while having an [authenticated][Actor.Authenticated] [Actor] would
 *   simply mean that the operation derived from the [HttpMethod] will be performed.
 * @param onAuthentication Action run whenever the [Actor] is authenticated. At this point, the most
 *   up-to-date [Actor] is probably different from the one with which the test has been configured
 *   and that is in the [CoreHttpClientTestScope] given to the [body].
 * @param body Callback run when the environment has been set up and is, therefore, ready to be
 *   used.
 * @see HttpClient.authenticateAndGet
 * @see HttpClient.authenticateAndPost
 * @see HttpClient.authenticateAndSubmitForm
 * @see HttpClient.authenticateAndSubmitFormWithBinaryData
 */
private fun <T : Actor> runCoreHttpClientTest(
  actor: T,
  onAuthentication: () -> Unit,
  body: suspend CoreHttpClientTestScope<T>.() -> Unit
) {
  val authorizer = TestAuthorizer()
  val actorProvider = FixedActorProvider(actor)
  val authenticator = TestAuthenticator(authorizer, actorProvider) { onAuthentication() }
  val authenticationLock = AuthenticationLock(authenticator, actorProvider)
  val instance = TestMastodonInstance(authorizer, authenticator, authenticationLock)
  val module =
    MastodonCoreModule(
      { TestHttpInstanceProvider(authorizer, authenticator, authenticationLock) },
      { authenticationLock }
    ) {
      SampleTermMuter()
    }
  Injector.register<CoreModule>(module)
  runTest { CoreHttpClientTestScope(delegate = this, instance.client, actor).body() }
  Injector.unregister<CoreModule>()
}
