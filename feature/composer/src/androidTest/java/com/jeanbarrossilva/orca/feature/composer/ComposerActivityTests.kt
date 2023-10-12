package com.jeanbarrossilva.orca.feature.composer

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextInputSelection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.jeanbarrossilva.orca.feature.composer.test.assertTextEquals
import com.jeanbarrossilva.orca.feature.composer.test.isBoldFormat
import com.jeanbarrossilva.orca.feature.composer.test.isItalicFormat
import com.jeanbarrossilva.orca.feature.composer.test.onField
import com.jeanbarrossilva.orca.feature.composer.test.onToolbar
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
internal class ComposerActivityTests {
  @get:Rule val composeRule = createAndroidComposeRule<ComposerActivity>()

  @Test
  fun stylesSelectedComposition() {
    composeRule.onField().performTextInput("Hello, world!")
    composeRule.onField().performTextInputSelection(TextRange(0, 5))
    composeRule.onToolbar().onChildren().filterToOne(isBoldFormat()).performClick()
    composeRule
      .onField()
      .assertTextEquals(
        buildAnnotatedString {
          withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Hello") }
          append(", world!")
        }
      )
  }

  @Ignore(
    "androidx.compose.ui:ui-text:1.5.0 has a bug that prevents stylization from being kept " +
      "across selection changes."
  )
  @Test
  fun keepsStylizationWhenUnselectingStylizedComposition() {
    composeRule.onField().performTextInput("Hello, world!")
    composeRule.onField().performTextInputSelection(TextRange(7, 12))
    composeRule.onToolbar().onChildren().filterToOne(isItalicFormat()).performClick()
    repeat(64) { composeRule.onField().performTextInputSelection(TextRange((0..12).random())) }
    composeRule
      .onField()
      .assertTextEquals(
        buildAnnotatedString {
          append("Hello, ")
          withStyle(SpanStyle(fontStyle = FontStyle.Italic)) { append("world") }
          append('!')
        }
      )
  }
}
