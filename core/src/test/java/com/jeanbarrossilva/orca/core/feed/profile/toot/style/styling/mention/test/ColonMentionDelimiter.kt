package com.jeanbarrossilva.orca.core.feed.profile.toot.style.styling.mention.test

import com.jeanbarrossilva.orca.core.feed.profile.toot.style.styling.mention.Mention

internal object ColonMentionDelimiter : Mention.Delimiter() {
    override fun getRegex(): Regex {
        return Regex(":[A-Z0-9._%+-]*")
    }

    override fun onGetUsername(match: String): String {
        return match.removePrefix(":")
    }
}