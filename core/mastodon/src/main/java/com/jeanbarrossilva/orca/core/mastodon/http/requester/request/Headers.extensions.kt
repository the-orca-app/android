package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

import io.ktor.http.Headers
import io.ktor.util.StringValues

/**
 * Creates [Headers] from the given [String].
 *
 * @param string [String] from which [Headers] will be created.
 */
internal fun Headers.Companion.valueOf(string: String): Headers {
  return build { appendAll(StringValues.valueOf(string)) }
}
