package pe.edu.upeu.data.session

import pe.edu.upeu.data.model.ClienteResponse

expect object SessionStore {
    fun saveCliente(cliente: ClienteResponse)
    fun loadCliente(): ClienteResponse?
    fun clear()
}
