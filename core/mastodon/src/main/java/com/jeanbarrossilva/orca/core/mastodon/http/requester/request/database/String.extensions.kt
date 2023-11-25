package com.jeanbarrossilva.orca.core.mastodon.http.requester.request.database

/**
 * Splits this [String] representation of a collection into each of its [String] elements (e. g.,
 * `"[[0, 1], [2, 3]]"` → `["0, 1", "2, 3"]`).
 *
 * @param prefix [String] that indicates the beginning of the collection.
 * @param suffix [String] that indicates the end of the collection.
 * @param separator [String] that separates each of the collection's elements.
 */
internal fun String.splitIntoElements(
  prefix: String = "[",
  suffix: String = "]",
  separator: String = ", "
): List<String> {
  val osi = OuterSeparatorExtractor.extract(this, prefix, suffix, separator)
  val split = mutableListOf<String>()
  var currentElementIndex = 0
  var currentSeparatorIndex = -1
  val isAtSeparator = { currentSeparatorIndex in 0..separator.lastIndex }
  forEachIndexed { index, char ->
    if (index in osi || isAtSeparator()) {
      currentSeparatorIndex++
    } else {
      currentSeparatorIndex = -1
    }
    if (currentSeparatorIndex == 0) {
      currentElementIndex++
    } else if (!isAtSeparator()) {
      if (split.getOrNull(currentElementIndex) == null) {
        split.add(currentElementIndex, "")
      }
      split[currentElementIndex] = split[currentElementIndex] + char
    }
  }
  return split.toList()
}
