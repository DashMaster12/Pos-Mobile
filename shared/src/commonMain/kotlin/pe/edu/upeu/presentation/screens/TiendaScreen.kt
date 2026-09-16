package pe.edu.upeu.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.presentation.components.DeliveryButton

data class ProductItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val priceStr: String
)

@Composable
fun TiendaScreen(
    isLoggedIn: Boolean = true,
    onAddToCart: (ProductItem) -> Unit = {},
    onLoginRequired: () -> Unit = {}
) {
    val sampleProducts = listOf(
        ProductItem("1", "Hamburguesa Deluxe", "Doble carne, queso cheddar y tocino crujiente", 25.0, "$25.00"),
        ProductItem("2", "Pizza Pepperoni", "Muzzarella fundida y rodajas de pepperoni artesanal", 45.0, "$45.00"),
        ProductItem("3", "Alitas BBQ", "Bañado en salsa barbacoa de la casa con papas", 22.5, "$22.50"),
        ProductItem("4", "Lomo Saltado", "Jugoso lomo fino flambeado con cebolla y tomate", 32.0, "$32.00")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "¡Menú Delivery!",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Elige tus 4 platos favoritos y recíbelos al instante",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Grid de 2 columnas para el catálogo exacto de 4 productos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(sampleProducts) { product ->
                Card(
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(36.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = product.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = product.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.height(32.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = product.priceStr,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        DeliveryButton(
                            text = "Agregar",
                            onClick = {
                                if (isLoggedIn) {
                                    onAddToCart(product)
                                } else {
                                    onLoginRequired()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(38.dp)
                        )
                    }
                }
            }
        }
    }
}
