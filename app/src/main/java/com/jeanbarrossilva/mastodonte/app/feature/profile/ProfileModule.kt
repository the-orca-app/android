package com.jeanbarrossilva.mastodonte.app.feature.profile

import com.jeanbarrossilva.mastodon.feature.profile.ProfileNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

@Suppress("FunctionName")
internal fun ProfileModule(): Module {
    return module {
        single<ProfileNavigator> {
            DefaultProfileNavigator(androidContext())
        }
    }
}