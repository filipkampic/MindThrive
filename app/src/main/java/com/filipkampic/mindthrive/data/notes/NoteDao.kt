package com.filipkampic.mindthrive.data.notes

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.filipkampic.mindthrive.model.notes.Note
import com.filipkampic.mindthrive.model.notes.NoteFolder
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :id")
    fun getNoteById(id: Int): Flow<Note?>

    @Query("SELECT * FROM notes WHERE id = :id LIMIT 1")
    suspend fun getNoteByIdOnce(id: Int): Note?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM note_folders")
    fun getAllNoteFolders(): Flow<List<NoteFolder>>

    @Query("SELECT * FROM notes WHERE folderId = :folderId")
    fun getNotesByFolderId(folderId: Int?): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE folderId IS NULL")
    fun getNotesWithoutFolder(): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNoteFolder(folder: NoteFolder)

    @Query("UPDATE note_folders SET name = :newName WHERE id = :folderId")
    suspend fun updateFolderName(folderId: Int, newName: String)

    @Delete
    suspend fun deleteFolder(folder: NoteFolder)

    @Query("UPDATE notes SET folderId = NULL WHERE folderId = :folderId")
    suspend fun clearFolderIdFromNotes(folderId: Int)

    @Query("DELETE FROM notes WHERE folderId = :folderId")
    suspend fun deleteNotesByFolderId(folderId: Int)
}