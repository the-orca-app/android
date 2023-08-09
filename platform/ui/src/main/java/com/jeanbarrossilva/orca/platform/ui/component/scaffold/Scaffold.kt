package com.jeanbarrossilva.orca.platform.ui.component.scaffold

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jeanbarrossilva.orca.platform.theme.OrcaTheme
import com.jeanbarrossilva.orca.platform.theme.extensions.plus
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.bar.TopAppBar
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.bar.text.AutoSizeText
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.snackbar.orcaVisuals
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.snackbar.presenter.SnackbarPresenter
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.snackbar.presenter.rememberSnackbarPresenter

/**
 * Orca-specific [Scaffold].
 *
 * @param modifier [Modifier] to be applied to the underlying [Scaffold].
 * @param floatingActionButton [FloatingActionButton] to be placed at the bottom, horizontally
 * centered.
 * @param topAppBar [TopAppBar] to be placed at the top.
 * @param snackbarPresenter [SnackbarPresenter] through which [Snackbar]s can be presented.
 * @param content Main content of the current context.
 **/
@Composable
fun Scaffold(
    modifier: Modifier = Modifier,
    floatingActionButton: @Composable () -> Unit,
    topAppBar: @Composable () -> Unit = { },
    snackbarPresenter: SnackbarPresenter = rememberSnackbarPresenter(),
    content: @Composable (padding: PaddingValues) -> Unit
) {
    Scaffold(
        modifier,
        topAppBar,
        snackbarHost = {
            SnackbarHost(snackbarPresenter.hostState) {
                Snackbar(
                    it,
                    shape = OrcaTheme.shapes.medium,
                    containerColor = it.orcaVisuals.containerColor,
                    dismissActionContentColor = it.orcaVisuals.contentColor
                )
            }
        },
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = FabPosition.Center,
        content = content
    )
}

/** Preview of a [Scaffold][com.jeanbarrossilva.orca.platform.ui.component.scaffold.Scaffold]. **/
@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun ScaffoldPreview() {
    val snackbarPresenter = rememberSnackbarPresenter()

    LaunchedEffect(snackbarPresenter) {
        snackbarPresenter.presentInfo("Info")
        snackbarPresenter.presentError("Error") { }
    }

    OrcaTheme {
        @Suppress("UnusedMaterial3ScaffoldPaddingParameter")
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = { }) {
                    Icon(OrcaTheme.Icons.Add, contentDescription = "Add")
                }
            },
            topAppBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar {
                    AutoSizeText("Scaffold")
                }
            },
            snackbarPresenter = snackbarPresenter
        ) {
            LazyColumn(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = it + OrcaTheme.overlays.fab
            ) {
                item {
                    Text("Content")
                }
            }
        }
    }
}
