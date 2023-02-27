package com.rmarinov.notes.ui.mapper

import com.rmarinov.notes.data.db.model.Note
import com.rmarinov.notes.ui.model.NoteUiModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteMapper @Inject constructor() {

    fun toUiModel(note: Note): NoteUiModel =
        NoteUiModel(
            id = note.id,
            title = note.title,
            content = note.content
        )

    fun toDbModel(note: NoteUiModel): Note =
        Note(
            id = note.id,
            title = note.title,
            content = note.content
        )
}