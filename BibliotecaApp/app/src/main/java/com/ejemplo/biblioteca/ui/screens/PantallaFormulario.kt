package com.ejemplo.biblioteca.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ejemplo.biblioteca.data.model.Libro
import com.ejemplo.biblioteca.ui.viewmodel.LibroViewModel
import com.ejemplo.biblioteca.ui.viewmodel.UiState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaFormulario(
    viewModel: LibroViewModel,
    libroId: Int?,
    onNavigateBack: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var autor by remember { mutableStateOf("") }
    var isbn by remember { mutableStateOf("") }
    var ejemplares by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("") }
    var anioPublicacion by remember { mutableStateOf("") }

    var tituloError by remember { mutableStateOf<String?>(null) }
    var autorError by remember { mutableStateOf<String?>(null) }
    var isbnError by remember { mutableStateOf<String?>(null) }
    var ejemplaresError by remember { mutableStateOf<String?>(null) }
    var generoError by remember { mutableStateOf<String?>(null) }
    var anioPublicacionError by remember { mutableStateOf<String?>(null) }

    val operacionState by viewModel.operacionState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(libroId) {
        if (libroId != null) {
            val state = viewModel.librosState.value
            if (state is UiState.Success) {
                state.data.find { it.id == libroId }?.let {
                    titulo = it.titulo ?: ""
                    autor = it.autor ?: ""
                    isbn = it.isbn ?: ""
                    ejemplares = it.ejemplares?.toString() ?: "0"
                    genero = it.genero ?: ""
                    anioPublicacion = it.anioPublicacion?.toString() ?: ""
                }
            }
        }
    }

    LaunchedEffect(operacionState) {
        if (operacionState is UiState.Error) {
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = (operacionState as UiState.Error).message,
                    actionLabel = "Entendido"
                )
                viewModel.resetOperacionState()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (libroId == null) "Registrar Libro" else "Modificar Ficha", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Detalles de la Obra",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            // TÍTULO
            OutlinedTextField(
                value = titulo,
                onValueChange = { titulo = it; tituloError = null },
                label = { Text("Título de la obra") },
                leadingIcon = { Icon(Icons.Default.Book, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                isError = tituloError != null,
                supportingText = { tituloError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // AUTOR
            OutlinedTextField(
                value = autor,
                onValueChange = { autor = it; autorError = null },
                label = { Text("Autor / Escritor") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                isError = autorError != null,
                supportingText = { autorError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // GÉNERO
            OutlinedTextField(
                value = genero,
                onValueChange = { genero = it; generoError = null },
                label = { Text("Género Literario") },
                leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                isError = generoError != null,
                supportingText = { generoError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // AÑO DE PUBLICACIÓN
            OutlinedTextField(
                value = anioPublicacion,
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        anioPublicacion = input
                        anioPublicacionError = null
                    }
                },
                label = { Text("Año de Publicación") },
                leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = anioPublicacionError != null,
                supportingText = { anioPublicacionError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // ISBN
            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it; isbnError = null },
                label = { Text("ISBN (Código)") },
                leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                isError = isbnError != null,
                supportingText = { isbnError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // EJEMPLARES
            OutlinedTextField(
                value = ejemplares,
                onValueChange = { ejemplares = it; ejemplaresError = null },
                label = { Text("Ejemplares Disponibles") },
                leadingIcon = { Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = ejemplaresError != null,
                supportingText = { ejemplaresError?.let { Text(it) } },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (operacionState is UiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = MaterialTheme.colorScheme.primary)
            } else {
                Button(
                    onClick = {
                        var esValido = true

                        if (titulo.trim().isEmpty()) {
                            tituloError = "El título es obligatorio"
                            esValido = false
                        }
                        if (autor.trim().isEmpty()) {
                            autorError = "El autor es obligatorio"
                            esValido = false
                        }
                        if (genero.trim().isEmpty()) {
                            generoError = "El género es obligatorio"
                            esValido = false
                        }

                        val anioNum = anioPublicacion.toIntOrNull()
                        if (anioNum == null || anioNum <= 0) {
                            anioPublicacionError = "Ingresa un año válido"
                            esValido = false
                        }
                        if (isbn.trim().isEmpty()) {
                            isbnError = "El ISBN es obligatorio"
                            esValido = false
                        }

                        val num = ejemplares.toIntOrNull()
                        if (num == null || num < 1) {
                            ejemplaresError = "Ingresa un número mayor a 0"
                            esValido = false
                        }

                        if (esValido) {
                            val libro = Libro(
                                id = libroId,
                                titulo = titulo,
                                autor = autor,
                                isbn = isbn,
                                ejemplares = num!!,
                                genero = genero,
                                anioPublicacion = anioNum!!
                            )

                            if (libroId == null) {
                                viewModel.registrarLibro(libro) {
                                    viewModel.resetOperacionState()
                                    onNavigateBack()
                                }
                            } else {
                                viewModel.actualizarLibro(libroId, libro) {
                                    viewModel.resetOperacionState()
                                    onNavigateBack()
                                }
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Guardar Ficha en Archivo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}