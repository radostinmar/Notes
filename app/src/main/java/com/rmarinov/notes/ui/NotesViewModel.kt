package com.rmarinov.notes.ui

import androidx.lifecycle.*
import com.rmarinov.notes.data.db.model.Note
import com.rmarinov.notes.domain.repository.NotesRepository
import com.rmarinov.notes.ui.mapper.NoteMapper
import com.rmarinov.notes.ui.model.NoteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val noteMapper: NoteMapper
) : ViewModel() {

    private val _notes: Flow<List<Note>> = notesRepository.loadAll()

    private val _selectedNotes = MutableStateFlow(emptySet<Long>())

    val notesRouteConfig: Flow<NotesRouteConfig> = _notes.combine(_selectedNotes) { notes, selectedNotes ->
        val noteIds = notes.map(Note::id)
        val noteUiModels = notes.map(noteMapper::toUiModel)

        NotesRouteConfig(
            notes = noteUiModels,
            selectedNotes = selectedNotes
                .filter { it in noteIds }
                .toSet()
        )
    }

    private val _openNote = MutableLiveData<Event<Long>>()
    val openNote: LiveData<Event<Long>> = _openNote

    fun onAddNoteClicked() {
        _openNote.value = Event(-1)
    }

    fun onNoteClicked(note: NoteUiModel) {
        _openNote.value = Event(note.id)
    }

    fun onNoteLongClicked(note: NoteUiModel) {
        _selectedNotes.value = if (note.id in _selectedNotes.value) {
            _selectedNotes.value - note.id
        } else {
            _selectedNotes.value + note.id
        }
    }

    fun onBackPressedWhenNotesAreSelected() {
        _selectedNotes.value = emptySet()
    }

    fun onDeleteClicked(selectedNote: Set<Long>) {
        if (selectedNote.isNotEmpty()) {
            viewModelScope.launch {
                notesRepository.deleteByIds(selectedNote)
                _selectedNotes.value = emptySet()
            }
        }
    }
}