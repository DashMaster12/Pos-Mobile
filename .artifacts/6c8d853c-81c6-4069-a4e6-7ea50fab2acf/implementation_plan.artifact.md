# Plan de Implementación: Creación Real de Pedidos Conectada al Cliente Logueado (`POST /pos-venta-service/order`)

Integración del flujo de confirmación de pedidos (`confirmOrder`) para enviar el `clienteId` real del usuario autenticado junto con los detalles de la compra (productos y cantidades) hacia el backend, permitiendo que aparezcan guardados en la base de datos de Supabase y visibles en el POS web.

## User Review Required

> [!IMPORTANT]
> - **Cliente ID Autenticado**: Se pasará el `id` real del `ClienteResponse` (del usuario logueado) al crear la orden (`CreateOrderRequest.clienteId`), en lugar del ID quemado `1L`.
> - **Manejo de Errores de Orden**: Si el backend de órdenes rechaza el pedido (por ejemplo, porque no hay una caja abierta `GeneralCashBox` en estado `OPEN`), la app mostrará un mensaje descriptivo al usuario en lugar de fallar silenciosamente.

## Proposed Changes

### ViewModel & Network (`shared/src/commonMain/kotlin/pe/edu/upeu/presentation/viewmodel/`)

#### [MODIFY] [AppViewModel.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/viewmodel/AppViewModel.kt)
- Actualizar `confirmOrder` para recibir `clienteId: Long?` y pasarlo en el objeto `CreateOrderRequest`.
- Capturar y reportar errores en caso de fallo del backend (ej. caja cerrada).

### Presentation (`shared/src/commonMain/kotlin/pe/edu/upeu/presentation/`)

#### [MODIFY] [CustomerMainContent.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/CustomerMainContent.kt)
- Pasar el `loggedInCliente?.id` al confirmar la orden desde la pantalla de carrito.

## Verification Plan

### Automated Tests
- Ejecutar `gradle_build` (`assembleDebug`) para validar compilación exitosa en Kotlin Multiplatform.
