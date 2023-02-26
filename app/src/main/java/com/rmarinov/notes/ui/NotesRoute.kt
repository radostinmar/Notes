package com.rmarinov.notes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.rmarinov.notes.R
import com.rmarinov.notes.data.db.model.Note
import com.rmarinov.notes.ui.composables.AppBarState

@Composable
fun NotesRoute(
    onComposing: (AppBarState) -> Unit,
    navigateToNote: (Long) -> Unit,
    notesViewModel: NotesViewModel = hiltViewModel()
) {
    notesViewModel.openNote.observe(
        LocalLifecycleOwner.current,
        EventObserver { navigateToNote(it) }
    )

    val notesRouteConfig = notesViewModel
        .notesRouteConfig
        .observeAsState(NotesRouteConfig(emptyList(), emptySet()))
        .value

    val selectedNotes = notesRouteConfig.selectedNotes

    val areNotesSelected = selectedNotes.isNotEmpty()

    BackHandler(enabled = areNotesSelected) {
        notesViewModel.onBackPressedWhenNotesAreSelected()
    }

    val appBarTitle = if (areNotesSelected) {
        selectedNotes.size.toString()
    } else {
        stringResource(R.string.notes)
    }

    LaunchedEffect(key1 = selectedNotes.size) {
        onComposing(
            AppBarState(
                title = appBarTitle,
                actions = {
                    if (areNotesSelected) {
                        IconButton(onClick = { notesViewModel.onDeleteClicked(selectedNotes) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete_note)
                            )
                        }
                    }
                }
            )
        )
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (list, button) = createRefs()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .constrainAs(list) {
                    start.linkTo(anchor = parent.start, margin = 16.dp)
                    end.linkTo(anchor = parent.end, margin = 16.dp)
                    top.linkTo(anchor = parent.top, margin = 16.dp)
                    bottom.linkTo(anchor = button.top, margin = 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
        ) {
            items(notesRouteConfig.notes, key = Note::id) { note ->
                NoteItem(
                    note = note,
                    onClick = { notesViewModel.onNoteClicked(note) },
                    onLongClick = { notesViewModel.onNoteLongClicked(note) },
                    isSelected = note.id in selectedNotes,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
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
            Text(text = stringResource(R.string.add_note))
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val borderColor = if (isPressed || isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    val hapticFeedback = LocalHapticFeedback.current

    Surface(
        border = BorderStroke(width = 1.dp, color = borderColor),
        color = if (isPressed || isSelected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.background
        },
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick,
                onLongClick = {
                    hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            ),
        shape = RoundedCornerShape(size = 4.dp)
    ) {
        Text(
            text = note.title.takeIf { it.isNotBlank() }
                ?: note.content.substringBefore("\n"),
            modifier = Modifier.padding(all = 16.dp),
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

data class NotesRouteConfig(
    val notes: List<Note>,
    val selectedNotes: Set<Long>
)
