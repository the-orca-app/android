package com.jeanbarrossilva.mastodonte.feature.profiledetails.viewmodel

import android.content.Context

/** Provides a [Context] through [provide]. **/
internal fun interface ContextProvider {
    /** Provides a [Context]. **/
    fun provide(): Context
}