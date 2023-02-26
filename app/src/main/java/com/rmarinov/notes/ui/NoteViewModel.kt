package com.rmarinov.notes.ui

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rmarinov.notes.data.db.model.Note
import com.rmarinov.notes.domain.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    val title = mutableStateOf("")
    val content = mutableStateOf("")
    val isNewNote = mutableStateOf(false)

    private val note = MutableStateFlow<Note?>(null)

    private val _close = MutableLiveData<Event<Unit>>()
    val close: LiveData<Event<Unit>> = _close

    fun init(noteId: Long) {
        viewModelScope.launch {
            notesRepository.get(noteId)?.let {
                note.value = it
                title.value = it.title
                content.value = it.content
                isNewNote.value = false
            } ?: run {
                isNewNote.value = true
                notesRepository.insert(Note(title = "", content = "")).let {
                    note.value = Note(id = it, title = "", content = "")
                }
            }

            note.filterNotNull()
                .collectLatest {
                    delay(SAVE_NOTE_DEBOUNCE)
                    notesRepository.update(it)
                }
        }
    }

    fun onTitleChanged(value: String) {
        title.value = value
        note.value = note.value?.copy(title = value)
    }

    fun onContentChanged(value: String) {
        content.value = value
        note.value = note.value?.copy(content = value)
    }

    fun onBackClickedWithEmptyNote(): Unit = deleteNote()

    fun onDeleteClicked(): Unit = deleteNote()

    private fun deleteNote() {
        note.value?.let {
            notesRepository.delete(it.id)
        }
        _close.postValue(Event(Unit))
    }

    companion object {
        private const val SAVE_NOTE_DEBOUNCE = 750L
    }
}