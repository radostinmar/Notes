package com.rmarinov.notes.ui.composables

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SharedAppBar(
    appBarState: AppBarState,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            AnimatedContent(
                targetState = appBarState.title,
                transitionSpec = {
                    fadeIn(animationSpec = tween(110, delayMillis = 45)) +
                            scaleIn(initialScale = 0.92f, animationSpec = tween(110, delayMillis = 45)) with
                            fadeOut(animationSpec = tween(90))
                }
            ) {
                Text(text = it, color = MaterialTheme.colorScheme.onPrimary)
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        actions = { appBarState.actions?.invoke(this) },
        modifier = modifier
    )
}

data class AppBarState(
    val title: String = "",
    val actions: (@Composable RowScope.() -> Unit)? = null
)