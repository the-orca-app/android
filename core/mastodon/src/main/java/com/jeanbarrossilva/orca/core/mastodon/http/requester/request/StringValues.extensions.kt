package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

import io.ktor.util.StringValues

/**
 * Creates [StringValues] from the given [string].
 *
 * @param string [String] version of a [StringValues].
 */
internal fun StringValues.Companion.valueOf(string: String): StringValues {
  return build {
    string
      .substringAfter(' ')
      .removeSurroundingBrackets()
      .splitIntoElements()
      .map {
        it.substringBefore('=') to
          it
            .substringAfter('=')
            .splitIntoElements(separator = "; ")
            .map(String::removeSurroundingBrackets)
      }
      .forEach { (name, values) -> appendAll(name, values) }
  }
}

/** Returns this [String] without the surrounding brackets. */
private fun String.removeSurroundingBrackets(): String {
  return substringAfter('[').substringBeforeLast(']')
}
