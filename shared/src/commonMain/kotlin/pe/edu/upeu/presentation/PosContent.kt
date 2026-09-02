package pe.edu.upeu.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosContent() {
    var isCartVisible by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            if (!isCartVisible) {
                ExtendedFloatingActionButton(
                    onClick = { isCartVisible = true },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Carrito") },
                    text = { Text("Ver Carrito") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // FONDO: Catálogo de Productos
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Punto de venta", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Registrar ventas rápidas", style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = true, onClick = { }, label = { Text("Todas") })
                    FilterChip(selected = false, onClick = { }, label = { Text("Hamburguesas") })
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(5) {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Placeholder simple sin emojis
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Hamburguesa Clásica", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("S/ 15.00", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // OVERLAY: Panel del Carrito superpuesto
            AnimatedVisibility(
                visible = isCartVisible,
                enter = slideInHorizontally(initialOffsetX = { -it }),
                exit = slideOutHorizontally(targetOffsetX = { -it }),
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Card(
                    shape = RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp)
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), RoundedCornerShape(0.dp, 16.dp, 16.dp, 0.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Carrito", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                            IconButton(onClick = { isCartVisible = false }) { Text("X", fontWeight = FontWeight.Bold) }
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Item en el carrito con placeholder
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(40.dp).background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Hamburguesa", fontWeight = FontWeight.Bold)
                                    Text("S/ 15.00", color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", fontWeight = FontWeight.Bold)
                            Text("S/ 15.00", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(8.dp)) {
                            Text("Confirmar venta")
                        }
                    }
                }
            }
        }
    }
}