package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

import io.ktor.http.Parameters
import io.ktor.util.StringValues

/**
 * Creates [Parameters] from the given [string].
 *
 * @param string [String] from which [Parameters] will be created.
 */
internal fun Parameters.Companion.valueOf(string: String): Parameters {
  return build { appendAll(StringValues.valueOf(string)) }
}
