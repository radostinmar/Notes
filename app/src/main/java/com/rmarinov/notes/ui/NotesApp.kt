package com.rmarinov.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.rmarinov.notes.ui.theme.NotesTheme

@Composable
fun NotesApp() {
    NotesTheme {
        val navController = rememberNavController()
        val navigationActions = remember(navController) {
            NotesNavigationActions(navController)
        }

        NotesNavGraph(
            navController = navController,
            navigateToNote = navigationActions.navigateToNote
        )
    }
}