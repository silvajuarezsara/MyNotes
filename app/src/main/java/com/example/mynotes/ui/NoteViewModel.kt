package com.example.mynotes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mynotes.data.Note
import com.example.mynotes.data.NoteRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    val notes = repository.notes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    fun save(note: Note) = viewModelScope.launch {
        if (note.id == 0L) repository.add(note) else repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    fun toggleCompleted(note: Note) = viewModelScope.launch {
        repository.update(note.copy(completed = !note.completed))
    }
}
