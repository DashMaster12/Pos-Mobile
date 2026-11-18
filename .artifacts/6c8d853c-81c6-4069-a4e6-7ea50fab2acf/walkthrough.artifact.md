# Walkthrough - Gestión Completa de Múltiples Direcciones y Formulario Validado

He integrado la gestión completa de múltiples direcciones en la app móvil y provisto el backend para actualización de direcciones.

## Cambios Realizados

### 1. Modelos y Red (`AuthDto.kt` y `ApiService.kt`)
- **`DireccionDto`**: Creado para serializar y deserializar direcciones (`id`, `direccion`, `referencia`, `esPrincipal`).
- **`ClienteResponse`**: Actualizado para incluir la lista de direcciones del cliente.
- **`ApiService`**: Incorporados métodos para listar, agregar, actualizar, eliminar y marcar direcciones como principales.

### 2. Formulario Bonito y Validado en Perfil (`UsuarioScreen.kt`)
- **Listado de Direcciones**: Tarjetas limpias que muestran las direcciones guardadas e identifican visualmente cuál es la dirección principal.
- **Formulario Modal de Agregar / Editar**: Diálogo flotante con validación en tiempo real (campo de dirección obligatorio, referencia opcional y checkbox de dirección principal).
- **Acciones**: Botones para editar, eliminar y agregar nuevas direcciones con sincronización en tiempo real con el servidor.

### 3. Sincronización en el Carrito (`CartScreen.kt` & `CustomerMainContent.kt`)
- Las direcciones reales obtenidas del backend se cargan en la pantalla de perfil y se sincronizan con las opciones de envío disponibles en el carrito de compras.

## Resultados de Verificación

### Pruebas de Compilación
- Ejecutado `gradle_build` con tarea `assembleDebug`:
  > [!NOTE]
  > **Build finished successfully.** El proyecto compila sin ningún error.
