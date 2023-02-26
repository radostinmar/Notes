package com.rmarinov.notes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.rmarinov.notes.R

@Composable
fun NoteRoute(
    onComposing: (ScaffoldState) -> Unit,
    close: () -> Unit,
    selectedNoteId: Long,
    noteViewModel: NoteViewModel = hiltViewModel()
) {
    noteViewModel.close.observe(LocalLifecycleOwner.current, EventObserver { close() })

    val appBarTitle = stringResource(R.string.edit_note)

    val isNoteEmpty = noteViewModel.title.value.isEmpty()
            && noteViewModel.content.value.isEmpty()

    BackHandler(enabled = isNoteEmpty && noteViewModel.isNewNote.value) {
        noteViewModel.onBackClickedWithEmptyNote()
    }

    LaunchedEffect(key1 = selectedNoteId) {
        noteViewModel.init(selectedNoteId)
    }

    LaunchedEffect(key1 = true) {
        onComposing(
            ScaffoldState(
                appBarTitle = appBarTitle,
                appBarActions = {
                    IconButton(onClick = { noteViewModel.onDeleteClicked() }) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = stringResource(R.string.delete_note)
                        )
                    }
                }
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            val (title, text) = createRefs()
            OutlinedTextField(
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.secondary
                ),
                value = noteViewModel.title.value,
                modifier = Modifier.constrainAs(title) {
                    start.linkTo(anchor = parent.start, margin = 16.dp)
                    end.linkTo(anchor = parent.end, margin = 16.dp)
                    top.linkTo(anchor = parent.top, margin = 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
                onValueChange = { value -> noteViewModel.onTitleChanged(value) }
            )
            OutlinedTextField(colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary
            ),
                value = noteViewModel.content.value,
                minLines = 10,
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(anchor = parent.start, margin = 16.dp)
                    end.linkTo(anchor = parent.end, margin = 16.dp)
                    top.linkTo(title.bottom, margin = 16.dp)
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                },
                onValueChange = { value -> noteViewModel.onContentChanged(value) })
        }
    }
}
