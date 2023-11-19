package com.jeanbarrossilva.orca.platform.autos.colors

import androidx.compose.ui.graphics.Color

/** [Color] version of this [Long]. */
val Long?.asColor: Color
  get() = this?.let(::Color) ?: Color.Unspecified