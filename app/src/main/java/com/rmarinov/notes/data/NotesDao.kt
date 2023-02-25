package com.rmarinov.notes.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.rmarinov.notes.data.db.model.Note

@Dao
interface NotesDao {

    @Query("SELECT * FROM Note")
    fun loadAll(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE id == :id")
    suspend fun get(id: Int): Note?

    @Insert(onConflict = REPLACE)
    suspend fun insert(note: Note): Long

    @Delete
    suspend fun delete(note: Note)
}