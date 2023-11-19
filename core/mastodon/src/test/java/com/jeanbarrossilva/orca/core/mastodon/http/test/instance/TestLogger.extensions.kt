package com.jeanbarrossilva.orca.core.mastodon.http.test.instance

import com.jeanbarrossilva.orca.core.mastodon.http.Logger

/** [Logger] returned by [test]. */
private val testLogger =
  object : Logger() {
    override fun onInfo(info: String) {}

    override fun onError(error: String) {}
  }

/** A no-op [Logger]. */
internal val Logger.Companion.test
  get() = testLogger
