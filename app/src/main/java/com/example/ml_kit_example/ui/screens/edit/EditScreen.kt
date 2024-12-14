package com.example.myapplication.screens.edit

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.entities.NoteData
import com.google.firebase.Firebase
import com.google.firebase.database.database
import java.util.Date

@SuppressLint("ComposeModifierMissing")
@Composable
fun EditScreen(navController: NavController, title: String?, content: String?) {
    var database =
        Firebase.database("https://my-note-9110e-default-rtdb.europe-west1.firebasedatabase.app/")
    var myRef = database.getReference("notes")
    var noteTitle by remember { mutableStateOf(title ?: "") }
    var noteContent by remember { mutableStateOf(content ?: "") }
    var context = LocalContext.current
    var addOrEdit = ""
    if (noteTitle.isEmpty() && noteContent.isEmpty()) {
        addOrEdit = "Ajouter"
    } else {
        addOrEdit = "Editer"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$addOrEdit une note",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextField(
                    value = noteTitle,
                    onValueChange = { newTitle -> noteTitle = newTitle },
                    label = { Text(text = "Titre de la note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = noteContent,
                    onValueChange = { newContent -> noteContent = newContent },
                    label = { Text(text = "Contenu de la note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if (noteTitle.isEmpty() || noteContent.isEmpty()) {
                            Toast.makeText(
                                context,
                                "Veuillez remplir les champs",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val date = Date()
                            val note = NoteData(noteTitle, noteContent, date.toString())
                            myRef.child(noteTitle).setValue(note)
                            Toast.makeText(
                                context,
                                "Note enregistrée",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate("home")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = "Save")
                }
            }
        }
    )
}
