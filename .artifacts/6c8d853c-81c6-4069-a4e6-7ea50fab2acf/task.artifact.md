# Tareas - Control Global de Sesión y Restricciones de Vistas

- [x] **Fase 1: Elevación de Estado de Sesión en CustomerMainContent**
  - [x] Declarar `isLoggedIn` compartido en `CustomerMainContent.kt`
  - [x] Añadir diálogo de aviso cuando el usuario invitado intenta realizar acciones protegidas

- [x] **Fase 2: Actualización de Pantallas con Validaciones de Sesión**
  - [x] Actualizar `TiendaScreen.kt` para validar `isLoggedIn` al agregar al carrito
  - [x] Actualizar `CartScreen.kt` para mostrar estado de invitado si `isLoggedIn == false`
  - [x] Actualizar `MisPedidosScreen.kt` para mostrar mensaje de inicio de sesión si `isLoggedIn == false`
  - [x] Sincronizar `UsuarioScreen.kt` con el estado global de sesión

- [x] **Fase 3: Verificación**
  - [x] Ejecutar `gradle_build` (`assembleDebug`) para validar compilación exitosa (Build finished successfully)
