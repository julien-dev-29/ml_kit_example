package com.example.ml_kit_example.ui.camera

import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.camera.core.AspectRatio
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.example.myapplication.entities.NoteData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.util.Date
import java.util.concurrent.CompletableFuture

@Composable
fun CameraScreen(navController: NavController, title: String?, content: String?) {
    CameraContent(navController, title, content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraContent(navController: NavController, title: String?, content: String?) {
    // Obtient le contexte actuel de l'application
    var context = LocalContext.current

// Obtient le propriétaire du cycle de vie actuel
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current

// Crée un contrôleur de caméra lié au cycle de vie de l'application
    val cameraController: LifecycleCameraController =
        remember { LifecycleCameraController(context) }

// Déclare une variable d'état pour stocker le texte détecté
    var detectedText: String by remember { mutableStateOf("No text detected yet..") }

// Initialise une référence à la base de données Firebase Realtime Database
    var database =
        Firebase.database("https://my-note-9110e-default-rtdb.europe-west1.firebasedatabase.app/")

// Obtient une référence à la collection "notes" dans la base de données
    var myRef = database.getReference("notes")

// Déclare une variable d'état pour stocker le titre de la note
    var noteTitle by remember { mutableStateOf(title ?: "") }

// Déclare une variable d'état pour stocker le contenu de la note
    var noteContent by remember { mutableStateOf(content ?: "") }

// Déclare une variable pour déterminer si l'action est "Ajouter" ou "Editer"
    var addOrEdit = ""

// Vérifie si le titre et le contenu de la note sont vides pour déterminer l'action
    if (noteTitle.isEmpty() && noteContent.isEmpty()) {
        addOrEdit = "Ajouter"
    } else {
        addOrEdit = "Editer"
    }


    fun onTextUpdated(updatedText: String) {
        detectedText = updatedText
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Text scanner: $noteTitle") }) },
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = androidx.compose.ui.Alignment.BottomCenter
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(Color.BLACK)
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                        scaleType = PreviewView.ScaleType.FILL_START
                    }.also { previewView ->
                        startTextRecognition(
                            context = context,
                            cameraController = cameraController,
                            lifecycleOwner = lifecycleOwner,
                            previewView = previewView,
                            onDetectedTextUpdated = ::onTextUpdated
                        )
                    }
                }
            )
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(androidx.compose.ui.graphics.Color.White)
                        .padding(16.dp),
                    text = detectedText,
                    color = androidx.compose.ui.graphics.Color.Black // Change the text color to black
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        noteTitle = detectedText.take(10)
                        noteContent = detectedText
                        if (noteTitle.isEmpty() || noteContent.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Veuillez remplir les champs",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val date = Date()
                            val note = NoteData(noteTitle, noteContent, date.toString())
                            val future = CompletableFuture<Void>()
                            myRef.child(noteTitle).setValue(note).addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    future.complete(null)
                                } else {
                                    future.completeExceptionally(task.exception)
                                }
                            }
                            future.thenAccept {
                                Toast.makeText(
                                    context,
                                    noteTitle,
                                    Toast.LENGTH_SHORT
                                ).show()
                                navController.navigate("home")
                            }.exceptionally { exception ->
                                Toast.makeText(
                                    context,
                                    "Erreur lors de l'enregistrement de la note ${exception.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                null
                            }
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Save")
                }
            }
        }
    }
}

private fun startTextRecognition(
    context: Context,
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onDetectedTextUpdated: (String) -> Unit
) {
    cameraController.imageAnalysisTargetSize = CameraController.OutputSize(AspectRatio.RATIO_16_9)
    cameraController.setImageAnalysisAnalyzer(
        ContextCompat.getMainExecutor(context),
        TextRecognitionAnalyser(onDetectedTextUpdated = onDetectedTextUpdated)
    )

    cameraController.bindToLifecycle(lifecycleOwner)
    previewView.controller = cameraController
}