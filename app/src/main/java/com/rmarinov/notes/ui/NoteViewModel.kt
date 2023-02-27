package com.rmarinov.notes.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmarinov.notes.data.db.model.Note
import com.rmarinov.notes.domain.repository.NotesRepository
import com.rmarinov.notes.ui.mapper.NoteMapper
import com.rmarinov.notes.ui.model.NoteUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository,
    private val noteMapper: NoteMapper
) : ViewModel() {

    private var isNewNote = false

    private val _note = MutableStateFlow(NoteUiModel(id = NO_NOTE_ID, title = "", content = ""))
    val note = _note.asStateFlow()

    private val _navigateBack = MutableLiveData<Event<Unit>>()
    val navigateBack: LiveData<Event<Unit>> = _navigateBack

    fun init(noteId: Long) {
        viewModelScope.launch {
            if (note.value.id == NO_NOTE_ID) {
                notesRepository.get(noteId)?.let {
                    _note.value = noteMapper.toUiModel(it)
                    isNewNote = false
                } ?: run {
                    isNewNote = true
                    notesRepository.insert(Note(title = "", content = "")).let {
                        _note.value = NoteUiModel(id = it, title = "", content = "")
                    }
                }
                note.debounce(SAVE_NOTE_DEBOUNCE_TIMEOUT).collect {
                    notesRepository.update(noteMapper.toDbModel(it))
                }
            }
        }
    }

    fun onTitleChanged(value: String) {
        _note.value = _note.value.copy(title = value)
    }

    fun onContentChanged(value: String) {
        _note.value = _note.value.copy(content = value)
    }

    fun onBackClicked() {
        if (note.value.title.isEmpty() && note.value.content.isEmpty() && isNewNote) {
            deleteNote()
        } else {
            viewModelScope.launch {
                notesRepository.update(noteMapper.toDbModel(note.value))
            }
            _navigateBack.value = Event(Unit)
        }
    }

    fun onDeleteClicked(): Unit = deleteNote()

    private fun deleteNote() {
        viewModelScope.launch {
            notesRepository.delete(_note.value.id)
        }
        _navigateBack.value = Event(Unit)
    }

    companion object {

        private const val SAVE_NOTE_DEBOUNCE_TIMEOUT = 1000L

        private const val NO_NOTE_ID = -1L
    }
}