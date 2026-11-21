# Plan de Implementación: Gestión Completa de Múltiples Direcciones (Frontend + Prompt Backend para Actualizar)

Implementar la gestión completa de múltiples direcciones (listar, agregar, editar y eliminar) en la app móvil (Frontend) y proveer el código/prompt para que Antigravity añada el método de actualización (`PUT`) en el backend de Spring Boot.

## User Review Required

> [!IMPORTANT]
> - **Prompt para Antigravity (Backend - Actualización de Dirección)**: Se añadirá el endpoint `PUT /api/clientes/{clienteId}/direcciones/{direccionId}` en `DireccionController` y `DireccionService`.
> - **Gestión en App Móvil (`UsuarioScreen`)**:
    - Listado en tiempo real de las direcciones guardadas del cliente.
    - Formulario modal bonito y validado con campos limpios: **Dirección** (obligatoria) y **Referencia** (opcional).
    - Acciones para Agregar, Editar, Eliminar y Marcar como principal.
- **Selección en Carrito (`CartScreen`)**:
    - El cliente podrá elegir su dirección de entrega entre las direcciones registradas en su cuenta.

## Proposed Changes

### Modelos y Red (`shared/src/commonMain/kotlin/pe/edu/upeu/data/`)

#### [MODIFY] [AuthDto.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/data/model/AuthDto.kt)
- Añadir `DireccionDto` (`id`, `direccion`, `referencia`, `esPrincipal`).
- Actualizar `ClienteResponse` para incluir `val direcciones: List<DireccionDto>? = null`.

#### [MODIFY] [ApiService.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/data/network/ApiService.kt`)
- Añadir métodos HTTP para listar, agregar, actualizar y eliminar direcciones.

### Vistas y Componentes (`shared/src/commonMain/kotlin/pe/edu/upeu/presentation/`)

#### [MODIFY] [UsuarioScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/UsuarioScreen.kt)
- Implementar la sección completa de direcciones con tarjetas, botón "Agregar dirección", modal con formulario validado para editar/agregar y opción de eliminar/marcar principal.

#### [MODIFY] [CartScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/CartScreen.kt)
- Sincronizar la selección de direcciones con la lista real de `direcciones` del cliente.

## Verification Plan

### Automated Tests
- Ejecutar compilación con `gradle_build` (`assembleDebug`).

### Manual Verification
- Comprobar que en la pantalla de usuario se puedan agregar, editar y eliminar direcciones con validación de formularios.
- Verificar el funcionamiento en el carrito al realizar un pedido.
