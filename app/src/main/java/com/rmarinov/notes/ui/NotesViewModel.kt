package com.rmarinov.notes.ui

import androidx.lifecycle.*
import com.rmarinov.notes.data.NotesDao
import com.rmarinov.notes.data.db.model.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesDao: NotesDao
) : ViewModel() {

    val notes: LiveData<String> = notesDao.loadAll().map { it.toString() }

    private val _openNote = MutableLiveData<Event<Int>>()
    val openNote: LiveData<Event<Int>> = _openNote

    fun onAddNoteClicked() {
        viewModelScope.launch {
            val newNoteId = notesDao.insert(Note(title = "test", content = ""))
            _openNote.postValue(Event(newNoteId.toInt()))
        }
    }
}