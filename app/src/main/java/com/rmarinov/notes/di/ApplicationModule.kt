package com.rmarinov.notes.di

import android.content.Context
import androidx.room.Room
import com.rmarinov.notes.data.NotesDao
import com.rmarinov.notes.data.db.NotesDatabase
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

    @Singleton
    @Provides
    fun provideNotesDatabase(
        @ApplicationContext applicationContext: Context
    ): NotesDatabase = Room.databaseBuilder(
        applicationContext,
        NotesDatabase::class.java, "notes"
    ).build()

    @Singleton
    @Provides
    fun provideNotesDao(
        notesDatabase: NotesDatabase
    ): NotesDao = notesDatabase.notesDao()

    @Singleton
    @Provides
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.IO
}