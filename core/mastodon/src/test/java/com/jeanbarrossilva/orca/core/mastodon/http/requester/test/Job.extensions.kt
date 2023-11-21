package com.jeanbarrossilva.orca.core.mastodon.http.requester.test

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin

/**
 * Starts the coroutine before the [usage] and cancels this [Job] after it's been used.
 *
 * @param I Receiver [Job].
 * @param O Result of the [usage].
 * @param usage Operation to be performed after the coroutine has been started and before this
 *   [Job]'s cancellation.
 */
internal suspend inline fun <I : Job, O> I.use(usage: I.() -> O): O {
  start()
  return usage().also { cancelAndJoin() }
}
