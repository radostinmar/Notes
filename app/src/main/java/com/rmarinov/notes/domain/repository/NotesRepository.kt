package com.rmarinov.notes.domain.repository

import android.util.Log
import androidx.work.*
import com.rmarinov.notes.data.NotesDao
import com.rmarinov.notes.data.db.model.Note
import com.rmarinov.notes.workers.DeleteNoteWorker
import com.rmarinov.notes.workers.DeleteNoteWorker.Companion.KEY_NOTE_ID
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NotesRepository @Inject constructor(
    private val notesDao: NotesDao,
    private val workManager: WorkManager
) {
    fun loadAll(): Flow<List<Note>> = notesDao.loadAll()

    suspend fun get(id: Long): Note? = notesDao.get(id)

    suspend fun insert(note: Note): Long = notesDao.insert(note)
        .also { Log.e("test", "insert: $it") }

    suspend fun update(note: Note): Unit = notesDao.update(note)

    fun delete(id: Long) {
        val data = Data.Builder()
            .putLong(KEY_NOTE_ID, id)
            .build()

        OneTimeWorkRequestBuilder<DeleteNoteWorker>()
            .setInputData(data)
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()
            .also(workManager::enqueue)
    }

    suspend fun deleteByIds(ids: Set<Long>) = notesDao.deleteByIds(ids)

}