package com.rmarinov.notes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.rmarinov.notes.R
import com.rmarinov.notes.data.db.model.Note

@Composable
fun NotesRoute(
    onComposing: (ScaffoldState) -> Unit,
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
            ScaffoldState(
                appBarTitle = appBarTitle,
                appBarActions = {
                    if (areNotesSelected) {
                        IconButton(onClick = { notesViewModel.onDeleteClicked(selectedNotes) }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.delete_note)
                            )
                        }
                    }
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = notesViewModel::onAddNoteClicked,
                        containerColor = MaterialTheme.colorScheme.primary,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = stringResource(R.string.add_note)
                        )
                    }
                }
            )
        )
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (list, topFade, bottomFade) = createRefs()
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(all = 12.dp),
            modifier = Modifier
                .constrainAs(list) {
                    start.linkTo(anchor = parent.start)
                    end.linkTo(anchor = parent.end)
                    top.linkTo(anchor = parent.top)
                    bottom.linkTo(anchor = parent.bottom)
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
        Spacer(
            Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            Color.Transparent
                        )
                    )
                )
                .constrainAs(topFade) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(12.dp)
                }
        )
        Spacer(
            Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .constrainAs(bottomFade) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(12.dp)
                }
        )
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
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

data class NotesRouteConfig(
    val notes: List<Note>,
    val selectedNotes: Set<Long>
)
