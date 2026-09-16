# Walkthrough - Control Global de Sesión y Restricciones de Vistas

He implementado con éxito la lógica de control de sesión simulada en toda la aplicación, restringiendo y gestionando de forma inteligente las funcionalidades interactivas cuando el usuario es invitado (`isLoggedIn == false`).

## Cambios Realizados

### 1. Estado Global de Sesión (`CustomerMainContent.kt`)
- Se elevó el estado booleano de sesión `isLoggedIn` (iniciando por defecto en `true`) a nivel del contenedor principal de la aplicación, compartiéndolo de manera unificada entre todas las pestañas y el carrito.

### 2. Restricciones en Tienda y Carrito
- **Tienda (`TiendaScreen.kt`)**: Si el usuario está como invitado (`isLoggedIn == false`), al intentar presionar *"Agregar"* en cualquier tarjeta de producto, la aplicación intercepta la acción y muestra un **diálogo de alerta** (*"Inicio de sesión requerido"*) invitándolo a iniciar sesión.
- **Carrito de Compras (`CartScreen.kt`)**: Si el usuario entra al carrito sin sesión activa, se muestra una vista ilustrativa de invitado con un botón de **"Iniciar Sesión"** que lo redirige a su perfil, deshabilitando el flujo de pago.

### 3. Restricción en Mis Pedidos (`MisPedidosScreen.kt`)
- Si el usuario cierra sesión, la pestaña **"Mis Pedidos"** oculta el historial de órdenes anteriores y muestra un estado vacío amigable con el mensaje *"Inicia sesión para ver tu historial de pedidos"* y un botón directo para iniciar sesión.

### 4. Sincronización en Perfil de Usuario (`UsuarioScreen.kt`)
- Sincronizado de forma bidireccional con el estado global: al pulsar *"Cerrar Sesión"* se actualiza la sesión a `false`, y al pulsar *"Iniciar Sesión"* se reactiva de manera interactiva.

---

## Resultados de Verificación

### Pruebas de Compilación
- Ejecutado `gradle_build` (`assembleDebug`):
  > [!NOTE]
  > **Build finished successfully.** El proyecto compila correctamente sin errores en Kotlin Multiplatform.
