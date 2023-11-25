package com.jeanbarrossilva.orca.core.mastodon.http.client

import android.util.Log

/** Logs messages with different severity levels. */
open class Logger {
  /**
   * Logs additional information.
   *
   * @param info [String] with the information to be logged.
   */
  internal fun info(info: String) {
    onInfo(info)
  }

  /**
   * Logs the occurrence of an error.
   *
   * @param error [String] describing the error to be logged.
   */
  internal fun error(error: String) {
    onError(error)
  }

  /**
   * Logs additional information.
   *
   * @param info [String] with the information to be logged.
   */
  protected open fun onInfo(info: String) {}

  /**
   * Logs the occurrence of an error.
   *
   * @param error [String] describing the error to be logged.
   */
  protected open fun onError(error: String) {}

  companion object {
    /** Tag to which logs sent by [android] will be attached. */
    const val ANDROID_LOGGER_TAG = "CoreHttpClient"

    /** [Logger] that uses the Android [Log]. */
    val android =
      object : Logger() {
        override fun onInfo(info: String) {
          Log.i(ANDROID_LOGGER_TAG, info)
        }

        override fun onError(error: String) {
          Log.e(ANDROID_LOGGER_TAG, error)
        }
      }
  }
}
