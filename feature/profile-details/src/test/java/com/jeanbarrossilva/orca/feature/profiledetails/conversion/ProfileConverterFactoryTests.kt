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

package com.jeanbarrossilva.orca.feature.profiledetails.conversion

import com.jeanbarrossilva.orca.autos.colors.Colors
import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.type.editable.EditableProfile
import com.jeanbarrossilva.orca.core.feed.profile.type.followable.FollowableProfile
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.sample
import com.jeanbarrossilva.orca.core.sample.test.feed.profile.type.sample
import com.jeanbarrossilva.orca.feature.profiledetails.ProfileDetails
import com.jeanbarrossilva.orca.feature.profiledetails.test.createSample
import com.jeanbarrossilva.orca.feature.profiledetails.test.sample
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ProfileConverterFactoryTests {
  private val coroutineScope = TestScope()

  @Test
  fun createdConverterConvertsDefaultProfile() {
    assertEquals(
      ProfileDetails.Default.sample,
      ProfileConverterFactory.create(coroutineScope).convert(Profile.sample, Colors.LIGHT)
    )
  }

  @Test
  fun createdConverterConvertsEditableProfile() {
    assertEquals(
      ProfileDetails.Editable.sample,
      ProfileConverterFactory.create(coroutineScope).convert(EditableProfile.sample, Colors.LIGHT)
    )
  }

  @Test
  fun createdConverterConvertsFollowableProfile() {
    val onStatusToggle = {}
    assertEquals(
      ProfileDetails.Followable.createSample(onStatusToggle),
      ProfileConverterFactory.create(coroutineScope)
        .convert(FollowableProfile.sample, Colors.LIGHT)
        .let { it as ProfileDetails.Followable }
        .copy(onStatusToggle = onStatusToggle)
    )
  }
}
