package com.rmarinov.notes.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NoteRoute(
    noteId: Int,
    noteViewModel: NoteViewModel = hiltViewModel()
) {
    noteViewModel.init(noteId)

    Text(text = noteViewModel.note.value)
}
