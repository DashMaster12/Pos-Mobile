package pe.edu.upeu.data.session

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pe.edu.upeu.data.model.ClienteResponse

private const val PREFS_NAME = "delicias_express_session"
private const val KEY_CLIENTE_JSON = "session_cliente_json"

object SessionStoreHost {
    private var prefs: SharedPreferences? = null

    fun bind(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    internal fun prefsOrNull(): SharedPreferences? = prefs
}

private val sessionJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

actual object SessionStore {
    actual fun saveCliente(cliente: ClienteResponse) {
        val encoded = sessionJson.encodeToString(cliente)
        prefsOrNull()?.edit()?.putString(KEY_CLIENTE_JSON, encoded)?.apply()
    }

    actual fun loadCliente(): ClienteResponse? {
        val raw = prefsOrNull()?.getString(KEY_CLIENTE_JSON, null) ?: return null
        return try {
            sessionJson.decodeFromString(ClienteResponse.serializer(), raw)
        } catch (_: Exception) {
            null
        }
    }

    actual fun clear() {
        prefsOrNull()?.edit()?.remove(KEY_CLIENTE_JSON)?.apply()
    }

    private fun prefsOrNull(): SharedPreferences? = SessionStoreHost.prefsOrNull()
}
