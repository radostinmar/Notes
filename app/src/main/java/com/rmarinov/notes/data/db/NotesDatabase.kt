package com.rmarinov.notes.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.rmarinov.notes.data.NotesDao
import com.rmarinov.notes.data.db.model.Note

@Database(entities = [Note::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun notesDao(): NotesDao
}