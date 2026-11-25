package pe.edu.upeu.data.session

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pe.edu.upeu.data.model.ClienteResponse
import platform.Foundation.NSUserDefaults

private const val KEY_CLIENTE_JSON = "session_cliente_json"

private val sessionJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

actual object SessionStore {
    actual fun saveCliente(cliente: ClienteResponse) {
        val encoded = sessionJson.encodeToString(cliente)
        NSUserDefaults.standardUserDefaults.setObject(encoded, KEY_CLIENTE_JSON)
    }

    actual fun loadCliente(): ClienteResponse? {
        val raw = NSUserDefaults.standardUserDefaults.stringForKey(KEY_CLIENTE_JSON) ?: return null
        return try {
            sessionJson.decodeFromString(ClienteResponse.serializer(), raw)
        } catch (_: Exception) {
            null
        }
    }

    actual fun clear() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(KEY_CLIENTE_JSON)
    }
}
