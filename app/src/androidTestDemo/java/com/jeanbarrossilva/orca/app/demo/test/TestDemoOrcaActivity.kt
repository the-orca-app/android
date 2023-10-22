package com.jeanbarrossilva.orca.app.demo.test

import androidx.core.view.children
import com.jeanbarrossilva.orca.app.demo.DemoOrcaActivity
import com.jeanbarrossilva.orca.platform.ui.core.content

internal class TestDemoOrcaActivity : DemoOrcaActivity() {
  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    keepFocus(hasFocus)
  }

  private fun keepFocus(isFocused: Boolean) {
    if (!isFocused) {
      content.children.first().requestFocus()
    }
  }
}