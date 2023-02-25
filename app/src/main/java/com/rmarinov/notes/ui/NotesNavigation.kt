package com.rmarinov.notes.ui

import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController

object NotesDestinations {
    const val NOTES_ROUTE = "notes"
    const val NOTE_ROUTE = "note"
}

class NotesNavigationActions(navController: NavHostController) {
    val navigateToNote: (noteId: Int) -> Unit = { noteId ->
        navController.navigate("${NotesDestinations.NOTE_ROUTE}/$noteId") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}