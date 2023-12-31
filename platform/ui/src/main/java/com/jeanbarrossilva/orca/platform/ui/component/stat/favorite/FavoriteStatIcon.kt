/*
 * Copyright © 2023 Orca
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If
 * not, see https://www.gnu.org/licenses.
 */

package com.jeanbarrossilva.orca.platform.ui.component.stat.favorite

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.jeanbarrossilva.orca.platform.autos.colors.asColor
import com.jeanbarrossilva.orca.platform.autos.iconography.asImageVector
import com.jeanbarrossilva.orca.platform.autos.theme.AutosTheme
import com.jeanbarrossilva.orca.platform.autos.theme.MultiThemePreview
import com.jeanbarrossilva.orca.platform.ui.R
import com.jeanbarrossilva.orca.platform.ui.component.stat.ActivateableStatIcon
import com.jeanbarrossilva.orca.platform.ui.component.stat.ActivateableStatIconColors
import com.jeanbarrossilva.orca.platform.ui.component.stat.ActivateableStatIconDefaults
import com.jeanbarrossilva.orca.platform.ui.component.stat.ActivateableStatIconInteractiveness
import com.jeanbarrossilva.orca.platform.ui.component.stat.Stat

/** Tag that identifies a [FavoriteStatIcon] for testing purposes. */
const val FAVORITE_STAT_ICON_TAG = "favorite-stat-icon"

/** Default values of a [FavoriteStatIcon]. */
internal object FavoriteStatIconDefaults {
  /**
   * [ActivateableStatIconColors] by which a [FavoriteStatIcon] is colored by default.
   *
   * @param inactiveColor [Color] to color it with when it's inactive.
   * @param activeColor [Color] to color it with when it's active.
   */
  @Composable
  fun colors(
    inactiveColor: Color = LocalContentColor.current,
    activeColor: Color = AutosTheme.colors.activation.favorite.asColor
  ): ActivateableStatIconColors {
    return ActivateableStatIconColors(inactiveColor, activeColor)
  }
}

/**
 * [ActivateableStatIcon] that represents a favorite [Stat].
 *
 * @param isActive Whether the state it represents is enabled.
 * @param interactiveness [ActivateableStatIconInteractiveness] that indicates whether this
 *   [ActivateableStatIconDefaults] can be interacted with.
 * @param colors [ActivateableStatIconColors] that defines the [Color]s to color it.
 * @param modifier [Modifier] to be applied to the underlying [ActivateableStatIconDefaults].
 */
@Composable
internal fun FavoriteStatIcon(
  isActive: Boolean,
  interactiveness: ActivateableStatIconInteractiveness,
  modifier: Modifier = Modifier,
  colors: ActivateableStatIconColors = FavoriteStatIconDefaults.colors()
) {
  ActivateableStatIcon(
    if (isActive) {
      AutosTheme.iconography.favorite.filled.asImageVector
    } else {
      AutosTheme.iconography.favorite.outlined.asImageVector
    },
    contentDescription = stringResource(R.string.platform_ui_favorite_stat),
    isActive,
    interactiveness,
    colors,
    modifier.testTag(FAVORITE_STAT_ICON_TAG)
  )
}

@Composable
@MultiThemePreview
private fun InactiveFavoriteStatIconPreview() {
  AutosTheme {
    Surface(color = AutosTheme.colors.background.container.asColor) {
      FavoriteStatIcon(isActive = false, ActivateableStatIconInteractiveness.Still)
    }
  }
}

@Composable
@MultiThemePreview
private fun ActiveFavoriteStatIconPreview() {
  AutosTheme {
    Surface(color = AutosTheme.colors.background.container.asColor) {
      FavoriteStatIcon(isActive = true, ActivateableStatIconInteractiveness.Still)
    }
  }
}
