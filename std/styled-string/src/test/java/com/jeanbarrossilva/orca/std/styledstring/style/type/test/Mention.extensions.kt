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

package com.jeanbarrossilva.orca.std.styledstring.style.type.test

import com.jeanbarrossilva.orca.std.styledstring.StyledString
import com.jeanbarrossilva.orca.std.styledstring.style.type.Mention
import java.net.URL

/**
 * Sample [URL] to create mentions with.
 *
 * @see StyledString.Builder.mention
 * @see Mention
 */
internal val Mention.Companion.url
  get() = URL("https://mastodon.social/@jeanbarrossilva")

/**
 * Sample username to mention.
 *
 * @see StyledString.Builder.mention
 * @see Mention
 */
internal val Mention.Companion.username
  get() = "jeanbarrossilva"
