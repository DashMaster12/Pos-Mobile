package pe.edu.upeu.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DireccionDto(
    val id: Long? = null,
    val direccion: String,
    val referencia: String? = null,
    val esPrincipal: Boolean? = false
)

@Serializable
data class ClienteRequest(
    val nombreCompleto: String,
    val tipoDocumento: String,
    val numeroDocumento: String,
    val direccion: String,
    val correo: String,
    val password: String
)

@Serializable
data class ClienteLoginRequest(
    val identifier: String,
    val password: String
)

@Serializable
data class ClienteResponse(
    val id: Long? = null,
    val nombreCompleto: String? = null,
    val tipoDocumento: String? = null,
    val numeroDocumento: String? = null,
    val direccion: String? = null,
    val correo: String? = null,
    val direcciones: List<DireccionDto>? = null
)
