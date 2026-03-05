package dev.capyide.mobile.ui

import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController(onSubscribeClick: () -> Unit = {}) =
    ComposeUIViewController { App(onSubscribeClick = onSubscribeClick) }
