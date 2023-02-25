package com.rmarinov.notes.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmarinov.notes.data.NotesDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesDao: NotesDao
) : ViewModel() {

    val note = mutableStateOf("")

    fun init(noteId: Int) {
        viewModelScope.launch {
            note.value = notesDao.get(noteId).toString()
        }
    }
}