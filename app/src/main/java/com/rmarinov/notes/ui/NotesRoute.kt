package com.rmarinov.notes.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.rmarinov.notes.data.db.model.Note

@Composable
fun NotesRoute(
    navigateToNote: (Int) -> Unit,
    notesViewModel: NotesViewModel = hiltViewModel()
) {
    notesViewModel.openNote.observe(LocalLifecycleOwner.current, EventObserver {
        navigateToNote(it)
    })

    val notes = notesViewModel.notes.observeAsState()
    Surface(color = MaterialTheme.colorScheme.background) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (text, button) = createRefs()
            Text(
                text = notes.value.orEmpty(),
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(anchor = parent.start, margin = 16.dp)
                    end.linkTo(anchor = parent.end, margin = 16.dp)
                    top.linkTo(anchor = parent.top, margin = 16.dp)
                    bottom.linkTo(anchor = button.top, margin = 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
            )
            Button(
                onClick = { notesViewModel.onAddNoteClicked() },
                modifier = Modifier.constrainAs(button) {
                    start.linkTo(anchor = parent.start, margin = 16.dp)
                    end.linkTo(anchor = parent.end, margin = 16.dp)
                    bottom.linkTo(anchor = parent.bottom, margin = 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(40.dp)
                },
                shape = RoundedCornerShape(size = 8.dp)
            ) {
                Text(text = "Add note")
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Text(text = note.title, Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
}
