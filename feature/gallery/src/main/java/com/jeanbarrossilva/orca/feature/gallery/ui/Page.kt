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

package com.jeanbarrossilva.orca.feature.gallery.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jeanbarrossilva.orca.core.feed.profile.post.content.Attachment
import com.jeanbarrossilva.orca.core.sample.feed.profile.post.content.samples
import com.jeanbarrossilva.orca.feature.gallery.R
import com.jeanbarrossilva.orca.platform.autos.theme.AutosTheme
import com.jeanbarrossilva.orca.std.imageloader.compose.Image
import com.jeanbarrossilva.orca.std.imageloader.compose.Sizing
import com.jeanbarrossilva.orca.std.imageloader.compose.rememberImageLoader
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.toggleScale
import net.engawapg.lib.zoomable.zoomable

@Composable
internal fun Page(
  entrypointIndex: Int,
  currentIndex: Int,
  secondary: List<Attachment>,
  onActionsVisibilityToggle: (areActionsVisible: Boolean) -> Unit,
  modifier: Modifier = Modifier,
  entrypoint: @Composable (Modifier, Sizing) -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  val zoomState = rememberZoomState()
  val isZoomedIn by zoomState.isZoomedInAsState
  val sizing = remember { Sizing.Widened }
  val pageModifier =
    modifier
      .zoomable(
        zoomState,
        onTap = {
          onActionsVisibilityToggle(isZoomedIn)
          if (isZoomedIn) {
            coroutineScope.launch { zoomState.reset() }
          }
        },
        onDoubleTap = {
          onActionsVisibilityToggle(isZoomedIn)
          zoomState.toggleScale(2.5f, position = it)
        }
      )
      .animateContentSize()
  if (currentIndex == entrypointIndex) {
    entrypoint(pageModifier, sizing)
  } else {
    Image(
      rememberImageLoader(
        secondary[currentIndex - if (currentIndex < entrypointIndex) 0 else 1].url
      ),
      contentDescription = stringResource(R.string.feature_gallery_attachment, currentIndex.inc()),
      pageModifier,
      sizing
    )
  }
}

@Composable
internal fun SampleEntrypoint(sizing: Sizing, modifier: Modifier = Modifier) {
  Image(
    rememberImageLoader(com.jeanbarrossilva.orca.std.imageloader.compose.R.drawable.image),
    contentDescription = stringResource(R.string.feature_gallery_attachment, 1),
    modifier,
    sizing
  )
}

@Composable
@Preview
private fun PagePreview() {
  AutosTheme {
    Page(
      entrypointIndex = 0,
      currentIndex = 1,
      secondary = Attachment.samples,
      onActionsVisibilityToggle = {}
    ) { modifier, sizing ->
      SampleEntrypoint(sizing, modifier)
    }
  }
}
