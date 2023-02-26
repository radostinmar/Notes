package com.rmarinov.notes.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.rmarinov.notes.ui.composables.AppBarState
import com.rmarinov.notes.ui.composables.SharedAppBar
import com.rmarinov.notes.ui.theme.NotesTheme

@Composable
fun NotesApp() {
    NotesTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            NotesNavigationActions(navController)
        }

        val appBarState = remember { mutableStateOf(AppBarState()) }

        Scaffold(
            topBar = { SharedAppBar(appBarState.value) },
            containerColor = MaterialTheme.colorScheme.background,
        ) { paddingValues ->
            NotesNavGraph(
                navController = navController,
                navigateToNote = navigationActions.navigateToNote,
                modifier = Modifier.padding(paddingValues),
                onComposing = { appBarState.value = it }
            )
        }
    }
}