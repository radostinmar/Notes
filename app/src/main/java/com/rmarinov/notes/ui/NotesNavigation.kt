package com.rmarinov.notes.ui

import androidx.navigation.NavHostController

object NotesDestinations {
    const val NOTES_ROUTE = "notes"
    const val NOTE_ROUTE = "note"
}

class NotesNavigationActions(navController: NavHostController) {
    val navigateToNote: (noteId: Long) -> Unit = { noteId ->
        navController.navigate("${NotesDestinations.NOTE_ROUTE}/$noteId")
    }
}