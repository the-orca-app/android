package com.jeanbarrossilva.orca.core.mastodon.http.requester.request

/**
 * Extracts the indices of outer separators within the [String] representation of a collection of
 * elements through [extract].
 */
internal object OuterSeparatorExtractor {
  /**
   * Extracts the indices of outer separators within the [String] representation of a collection of
   * elements.
   *
   * @param representation [String] representation of the collection.
   * @param prefix [String] that indicates the beginning of the collection.
   * @param suffix [String] that indicates the end of the collection.
   * @param separator [String] that separates each of the collection's elements.
   */
  fun extract(
    representation: String,
    prefix: String,
    suffix: String,
    separator: String
  ): Sequence<Int> {
    return sequence {
      var isWithinArrayElement = false
      with(representation) {
        for (index in 0..lastIndex) {
          when {
            index > 0 && substring(index).startsWith(prefix) -> isWithinArrayElement = true
            substring(index).let { index + lastIndex > it.length && it.startsWith(suffix) } ->
              isWithinArrayElement = false
            substring(index).startsWith(separator) ->
              if (!isWithinArrayElement) {
                yield(index)
              }
          }
        }
      }
    }
  }
}
