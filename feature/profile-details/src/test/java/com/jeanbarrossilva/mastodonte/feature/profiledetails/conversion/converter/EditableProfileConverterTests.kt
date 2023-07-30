package com.jeanbarrossilva.mastodonte.feature.profiledetails.conversion.converter

import com.jeanbarrossilva.mastodonte.core.profile.Profile
import com.jeanbarrossilva.mastodonte.core.profile.type.edit.EditableProfile
import com.jeanbarrossilva.mastodonte.core.profile.type.follow.FollowableProfile
import com.jeanbarrossilva.mastodonte.core.sample.profile.sample
import com.jeanbarrossilva.mastodonte.core.sample.profile.type.edit.sample
import com.jeanbarrossilva.mastodonte.core.sample.profile.type.follow.sample
import com.jeanbarrossilva.mastodonte.feature.profiledetails.ProfileDetails
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class EditableProfileConverterTests {
    private val converter = EditableProfileConverter(next = null)

    @Test
    fun convertsEditableProfile() {
        assertEquals(ProfileDetails.Editable.sample, converter.convert(EditableProfile.sample))
    }

    @Test
    fun doesNotConvertDefaultProfile() {
        assertNull(converter.convert(Profile.sample))
    }

    @Test
    fun doesNotConvertFollowableProfile() {
        assertNull(converter.convert(FollowableProfile.sample))
    }
}
