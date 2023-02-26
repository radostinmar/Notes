package com.rmarinov.notes.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NotesNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NotesDestinations.NOTES_ROUTE,
    navigateToNote: (Long) -> Unit,
    onComposing: (ScaffoldState) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(NotesDestinations.NOTES_ROUTE) {
            NotesRoute(
                navigateToNote = navigateToNote,
                onComposing = onComposing
            )
        }

        composable(
            route = "${NotesDestinations.NOTE_ROUTE}/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getLong("noteId")
                ?: throw IllegalStateException("Note opened without note id.")

            NoteRoute(
                selectedNoteId = noteId,
                close = { navController.popBackStack() },
                onComposing = onComposing
            )
        }
    }
}