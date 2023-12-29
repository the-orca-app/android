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

package com.jeanbarrossilva.orca.feature.composer.test

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import com.jeanbarrossilva.orca.feature.composer.COMPOSER_FIELD
import com.jeanbarrossilva.orca.feature.composer.Composer
import com.jeanbarrossilva.orca.feature.composer.ui.COMPOSER_TOOLBAR
import com.jeanbarrossilva.orca.feature.composer.ui.Toolbar

/** [SemanticsNodeInteraction] of a [Composer]'s field. */
internal fun ComposeTestRule.onField(): SemanticsNodeInteraction {
  return onNodeWithTag(COMPOSER_FIELD)
}

/** [SemanticsNodeInteraction] of a [Toolbar]. */
internal fun ComposeTestRule.onToolbar(): SemanticsNodeInteraction {
  return onNodeWithTag(COMPOSER_TOOLBAR)
}