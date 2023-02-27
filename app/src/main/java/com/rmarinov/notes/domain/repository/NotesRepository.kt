package com.rmarinov.notes.domain.repository

import com.rmarinov.notes.data.NotesDao
import com.rmarinov.notes.data.db.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val sharedScope: CoroutineScope
) {
    fun loadAll(): Flow<List<Note>> = notesDao.loadAll()

    suspend fun get(id: Long): Note? = notesDao.get(id)

    suspend fun insert(note: Note): Long = notesDao.insert(note)

    suspend fun update(note: Note): Unit =
        sharedScope.launch {
            notesDao.update(note)
        }.join()

    suspend fun delete(id: Long): Unit =
        sharedScope.launch {
            notesDao.delete(id)
        }.join()

    suspend fun deleteByIds(ids: Set<Long>) = notesDao.deleteByIds(ids)

}