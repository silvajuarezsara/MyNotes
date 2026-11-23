package com.example.mynotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import com.example.mynotes.data.Note
import com.example.mynotes.data.NoteDatabase
import com.example.mynotes.data.NoteRepository
import com.example.mynotes.ui.NoteViewModel
import com.example.mynotes.ui.NoteViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val viewModel: NoteViewModel by viewModels {
        NoteViewModelFactory(
            NoteRepository(NoteDatabase.getInstance(applicationContext).noteDao())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF283593),
                    secondary = Color(0xFF2E7D32),
                    background = Color(0xFFF5F5F5)
                )
            ) {
                NotesApp(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesApp(viewModel: NoteViewModel) {
    val notes by viewModel.notes.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<Note?>(null) }
    var showEditor by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val filtered = notes.filter {
        it.title.contains(query, ignoreCase = true) ||
            it.content.contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MyNotes") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editing = null
                showEditor = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar nota")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar notas") },
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (query.isBlank()) "Aún no hay notas. Presiona + para crear una."
                    else "No se encontraron notas.")
                }
            } else {
                val width = LocalConfiguration.current.screenWidthDp
                if (width >= 600) {
                    // En pantallas amplias se presenta una cuadrícula de dos columnas.
                    androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                        columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(280.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filtered, key = { it.id }) { note ->
                            NoteItem(
                                note = note,
                                onEdit = { editing = note; showEditor = true },
                                onDelete = { viewModel.delete(note) },
                                onComplete = { viewModel.toggleCompleted(note) }
                            )
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(filtered, key = { it.id }) { note ->
                            NoteItem(
                                note = note,
                                onEdit = { editing = note; showEditor = true },
                                onDelete = { viewModel.delete(note) },
                                onComplete = { viewModel.toggleCompleted(note) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showEditor) {
        NoteEditor(
            initial = editing,
            onDismiss = { showEditor = false },
            onSave = { note ->
                viewModel.save(note)
                showEditor = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteItem(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                visible = false
                onDelete()
                true
            } else false
        }
    )

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                Box(
                    Modifier.fillMaxSize()
                        .background(Color(0xFFB3261E), RoundedCornerShape(16.dp))
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White)
                }
            },
            content = {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                note.title,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (note.completed) TextDecoration.LineThrough else TextDecoration.None
                            )
                            IconButton(onClick = onEdit) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }
                            Checkbox(checked = note.completed, onCheckedChange = { onComplete() })
                        }
                        Text(
                            note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (note.completed) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(note.date)),
                            style = MaterialTheme.typography.labelSmall
                        )
                        if (note.imageUri != null) {
                            Spacer(Modifier.height(8.dp))
                            AsyncImage(
                                model = note.imageUri,
                                contentDescription = "Imagen adjunta",
                                modifier = Modifier.fillMaxWidth().height(150.dp)
                            )
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun NoteEditor(
    initial: Note?,
    onDismiss: () -> Unit,
    onSave: (Note) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title.orEmpty()) }
    var content by remember { mutableStateOf(initial?.content.orEmpty()) }
    var imageUri by remember { mutableStateOf(initial?.imageUri) }
    var error by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        imageUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Nueva nota" else "Editar nota") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = false },
                    label = { Text("Título") },
                    isError = error,
                    singleLine = true
                )
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Contenido") },
                    minLines = 3
                )
                TextButton(onClick = {
                    picker.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
                    Text("Adjuntar imagen")
                }
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Vista previa",
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                }
                if (error) Text("El título es obligatorio.", color = MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (title.isBlank()) {
                    error = true
                } else {
                    onSave(
                        Note(
                            id = initial?.id ?: 0,
                            title = title.trim(),
                            content = content.trim(),
                            date = initial?.date ?: System.currentTimeMillis(),
                            completed = initial?.completed ?: false,
                            imageUri = imageUri
                        )
                    )
                }
            }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
