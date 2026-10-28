package pe.edu.upeu.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    val id: Long,
    val name: String? = null
)

@Serializable
data class ImageRefDto(
    val id: Long? = null,
    val name: String? = null,
    val contentType: String? = null
)

@Serializable
data class ProductDto(
    val id: Long,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null,
    val image: ImageRefDto? = null,
    val category: CategoryDto? = null
) {
    val priceStr: String get() = "$${price}0"

    val fullImageUrl: String?
        get() {
            val base = "https://ale-burgers-system.onrender.com/pos-venta-service"
            return when {
                !imageUrl.isNullOrBlank() -> {
                    if (imageUrl.startsWith("http")) imageUrl 
                    else if (imageUrl.startsWith("/")) "$base$imageUrl" 
                    else "$base/$imageUrl"
                }
                image?.id != null -> "$base/product-image/${image.id}"
                else -> null
            }
        }
}
