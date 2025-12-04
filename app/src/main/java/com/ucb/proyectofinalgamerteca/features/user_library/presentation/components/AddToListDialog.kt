package com.ucb.proyectofinalgamerteca.features.user_library.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ucb.proyectofinalgamerteca.features.user_library.domain.model.CustomGameList

@Composable
fun AddToListDialog(
    lists: List<CustomGameList>,
    onDismiss: () -> Unit,
    onListSelected: (CustomGameList) -> Unit,
    onCreateList: (String, String, Boolean) -> Unit
) {
    var newListName by remember { mutableStateOf("") }
    var newListDesc by remember { mutableStateOf("") }
    var isCreating by remember { mutableStateOf(false) }
    var isPublic by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Añadir a una lista",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de listas existentes
                if (!isCreating) {
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 200.dp)
                    ) {
                        items(lists) { list ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onListSelected(list) }
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = list.name, style = MaterialTheme.typography.bodyLarge, color = Color.Black)
                                    if(list.isPublic) {
                                        Text(text = "Pública", style = MaterialTheme.typography.labelSmall, color = Color(0xFFE52128))
                                    }
                                }
                            }
                            Divider(color = Color.LightGray.copy(alpha = 0.5f))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // NUEVA LISTA
                if (isCreating) {
                    OutlinedTextField(
                        value = newListName,
                        onValueChange = { newListName = it },
                        label = { Text("Nombre de la lista") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = newListDesc,
                        onValueChange = { newListDesc = it },
                        label = { Text("Descripción (Opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { isPublic = !isPublic }
                    ) {
                        Checkbox(
                            checked = isPublic,
                            onCheckedChange = { isPublic = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE52128))
                        )
                        Text(text = "Lista Pública", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { isCreating = false }) { Text("Cancelar", color = Color.Gray) }
                        Button(
                            onClick = {
                                // ✅ PASAMOS LOS 3 PARÁMETROS
                                onCreateList(newListName, newListDesc, isPublic)
                                // Reset
                                newListName = ""
                                newListDesc = ""
                                isPublic = false
                                isCreating = false
                            },
                            enabled = newListName.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE52128))
                        ) { Text("Crear") }
                    }
                } else {
                    Button(
                        onClick = { isCreating = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE52128))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear nueva lista")
                    }
                }
            }
        }
    }
}