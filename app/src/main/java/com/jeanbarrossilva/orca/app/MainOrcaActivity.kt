package com.jeanbarrossilva.orca.app

import androidx.lifecycle.lifecycleScope
import com.jeanbarrossilva.orca.app.module.core.MainMastodonCoreModule
import com.jeanbarrossilva.orca.core.mastodon.authenticationLock
import com.jeanbarrossilva.orca.core.mastodon.http.requester.Requester
import com.jeanbarrossilva.orca.core.mastodon.instance.MastodonInstanceProvider
import com.jeanbarrossilva.orca.core.mastodon.instanceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class MainOrcaActivity : OrcaActivity() {
  override val coreModule = MainMastodonCoreModule

  override fun onResume() {
    super.onResume()
    withRequester(Requester::resume)
  }

  override fun onDestroy() {
    super.onDestroy()
    withRequester(Requester::cancelAll)
  }

  private fun withRequester(action: suspend (Requester) -> Unit) {
    lifecycleScope.launch(Dispatchers.IO) {
      with(coreModule) {
        with(authenticationLock()) {
          scheduleUnlock {
            (instanceProvider() as MastodonInstanceProvider)
              .provide()
              .requester
              .authenticated(this)
              .run { action(this) }
          }
        }
      }
    }
  }
}
