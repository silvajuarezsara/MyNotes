package com.example.mynotes.data

class NoteRepository(private val dao: NoteDao) {
    val notes = dao.observeAll()
    suspend fun add(note: Note) = dao.insert(note)
    suspend fun update(note: Note) = dao.update(note)
    suspend fun delete(note: Note) = dao.delete(note)
}
