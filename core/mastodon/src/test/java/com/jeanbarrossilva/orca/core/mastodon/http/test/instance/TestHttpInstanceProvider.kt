package com.jeanbarrossilva.orca.core.mastodon.http.test.instance

import com.jeanbarrossilva.orca.core.auth.AuthenticationLock
import com.jeanbarrossilva.orca.core.instance.InstanceProvider
import com.jeanbarrossilva.orca.core.test.TestAuthenticator
import com.jeanbarrossilva.orca.core.test.TestAuthorizer

/**
 * [InstanceProvider] that provides a [TestMastodonInstance].
 *
 * @param authorizer [TestAuthorizer] with which the user will be authorized.
 * @param authenticator [TestAuthenticator] through which authentication can be done.
 * @param authenticationLock [AuthenticationLock] by which features can be locked or unlocked by an
 *   authentication "wall".
 */
internal class TestHttpInstanceProvider(
  private val authorizer: TestAuthorizer,
  private val authenticator: TestAuthenticator,
  private val authenticationLock: AuthenticationLock<TestAuthenticator>
) : InstanceProvider {
  override fun provide(): TestMastodonInstance {
    return TestMastodonInstance(authorizer, authenticator, authenticationLock)
  }
}
