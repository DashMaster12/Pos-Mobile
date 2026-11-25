package pe.edu.upeu.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import pe.edu.upeu.data.model.ProductDto
import pe.edu.upeu.presentation.components.DeliveryButton
import pe.edu.upeu.presentation.components.ExtraOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCustomizationScreen(
    product: ProductDto,
    allProducts: List<ProductDto> = emptyList(),
    onBackClick: () -> Unit,
    onAddToCart: (customPrice: Double, customizationSummary: String) -> Unit
) {
    // Listas de Cremas disponibles
    val availableCreams = listOf("Mayonesa", "Ketchup", "Mostaza", "Ají", "Aceituna")
    val selectedCreams = remember { mutableStateListOf<String>() }

    // Extras dinámicos traídos de la BD (categoría "Extras")
    val dbExtras = allProducts.filter { it.category?.name.equals("Extras", ignoreCase = true) }
    val availableExtras = if (dbExtras.isNotEmpty()) {
        dbExtras.map { ExtraOption(it.name, it.price) }
    } else {
        listOf(
            ExtraOption("Extra carne", 2.00),
            ExtraOption("Queso cheddar extra", 1.50),
            ExtraOption("Tocino crocante", 2.50)
        )
    }
    val selectedExtras = remember { mutableStateListOf<ExtraOption>() }

    // Notas adicionales para cocina
    var kitchenNotes by remember { mutableStateOf("") }

    // Cálculo del precio final en tiempo real (Base + Extras)
    val basePrice = product.price
    val extrasTotal = selectedExtras.sumOf { it.price }
    val finalUnitPrice = basePrice + extrasTotal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Personalizar Plato", 
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Regresar", 
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Precio Total:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "$${finalUnitPrice}0",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    DeliveryButton(
                        text = "Añadir al Carrito",
                        onClick = {
                            val creamsStr = if (selectedCreams.isNotEmpty()) "Cremas: ${selectedCreams.joinToString(", ")}" else ""
                            val extrasStr = if (selectedExtras.isNotEmpty()) "Extras: ${selectedExtras.joinToString(", ") { it.name }}" else ""
                            val notesStr = if (kitchenNotes.isNotBlank()) "Nota: $kitchenNotes" else ""

                            val summaryList = listOf(creamsStr, extrasStr, notesStr).filter { it.isNotBlank() }
                            val finalNotes = summaryList.joinToString(" | ")

                            onAddToCart(finalUnitPrice, finalNotes)
                        },
                        modifier = Modifier.width(200.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Imagen del producto
            val imgUrl = product.fullImageUrl
            if (!imgUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imgUrl,
                    contentDescription = product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = product.name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = product.description,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // --- SECCIÓN 1: CREMAS ---
            Text(
                text = "Elige tus Cremas (Opcional)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            availableCreams.forEach { cream ->
                val isChecked = selectedCreams.contains(cream)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isChecked) selectedCreams.remove(cream)
                            else selectedCreams.add(cream)
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            if (checked) selectedCreams.add(cream)
                            else selectedCreams.remove(cream)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = cream, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // --- SECCIÓN 2: EXTRAS DINÁMICOS DE LA BD ---
            Text(
                text = "Adicionales y Extras",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            availableExtras.forEach { extra ->
                val isChecked = selectedExtras.contains(extra)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (isChecked) selectedExtras.remove(extra)
                            else selectedExtras.add(extra)
                        }
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = { checked ->
                                if (checked) selectedExtras.add(extra)
                                else selectedExtras.remove(extra)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = extra.name, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                    Text(
                        text = "+$${extra.price}0",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(16.dp))

            // --- SECCIÓN 3: NOTAS DE COCINA ---
            Text(
                text = "Notas para Cocina",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = kitchenNotes,
                onValueChange = { kitchenNotes = it },
                placeholder = { Text("Ej. Sin cebolla, bien doradito...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
