package com.rmarinov.notes.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.rmarinov.notes.data.NotesDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class DeleteNoteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val notesDao: NotesDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val noteId = inputData.getLong(KEY_NOTE_ID, -1)
            notesDao.delete(noteId)
            Result.success()
        } catch (_: Exception) {
            Result.failure()
        }
    }

    companion object {
        const val KEY_NOTE_ID = "NOTE_ID"
    }
}