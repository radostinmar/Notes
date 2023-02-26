package com.rmarinov.notes.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.rmarinov.notes.data.db.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    @Query("SELECT * FROM Note")
    fun loadAll(): Flow<List<Note>>

    @Query("SELECT * FROM Note WHERE id == :id")
    suspend fun get(id: Long): Note?

    @Insert(onConflict = REPLACE)
    suspend fun insert(note: Note): Long

    @Update
    suspend fun update(note: Note)

    @Query("DELETE FROM Note WHERE id == :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM Note WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: Set<Long>)
}