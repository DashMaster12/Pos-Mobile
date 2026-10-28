# Tareas - Creación Real de Pedidos con Cliente Logueado

- [x] **Fase 1: Actualización de AppViewModel y CustomerMainContent**
  - [x] Modificar `confirmOrder` en `AppViewModel.kt` para aceptar `clienteId: Long` y propagar errores si ocurren
  - [x] Actualizar la llamada a `confirmOrder` en `CustomerMainContent.kt` enviando el `loggedInCliente?.id`

- [x] **Fase 2: Verificación**
  - [x] Ejecutar `gradle_build` (`assembleDebug`) (Build finished successfully)
