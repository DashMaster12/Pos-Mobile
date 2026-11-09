# Plan de Implementación: Flujo Completo de Pedidos, Seguimiento de Estados e Historial Segregado

Implementación integral de la creación de pedidos, consulta en tiempo real por cliente (`GET /pos-venta-service/order/cliente/{clienteId}`), banner de pedido activo en la pantalla principal y segregación entre **Pedidos En Curso** e **Historial de Pedidos** en `MisPedidosScreen`.

## User Review Required

> [!IMPORTANT]
> - **DTOs de Orden (`OrderDto.kt`)**:
>   - Eliminación total de `orderType`.
>   - Adición de `customerName`, `customerDocument`, `customerAddress` y `kitchenNotes` en `CreateOrderRequest`.
>   - Mapeo completo de `OrderResponse` con `status` (`EN_PREPARACION`, `EN_CAMINO`, `ENTREGADO`, `CANCELADO`), fecha, detalles de productos e importes.
> - **Consumo de API (`ApiService.kt` & `AppViewModel.kt`)**:
>   - Método `getOrdersByCliente(clienteId: Long)` para obtener las órdenes en tiempo real del backend.
>   - Método `confirmOrder` enriquecido con los datos del cliente activo (`nombreCompleto`, `numeroDocumento`, `direccion`).
> - **Pantalla Principal (Tienda / Home)**:
>   - Banner o tarjeta destacada en la parte superior cuando existan pedidos activos (`EN_PREPARACION` o `EN_CAMINO`) con el estado en vivo ("🛵 Tu pedido se está preparando en cocina...", "🚚 ¡El repartidor va en camino!").
> - **Pantalla Mis Pedidos (`MisPedidosScreen.kt`)**:
>   - Pestañas o secciones divididas:
>     - **Pestaña "En Curso"**: Muestra únicamente órdenes con estado `EN_PREPARACION` o `EN_CAMINO`.
>     - **Pestaña "Historial"**: Muestra órdenes con estado `ENTREGADO` o `CANCELADO`.

## Proposed Changes

### Data Models & Network (`shared/src/commonMain/kotlin/pe/edu/upeu/data/`)

#### [MODIFY] [OrderDto.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/data/model/OrderDto.kt)
- Reestructurar `CreateOrderRequest` sin `orderType` e incluyendo `customerName`, `customerDocument`, `customerAddress` y `kitchenNotes` en cada detalle.
- Crear DTOs completos para deserializar `OrderResponse`, `OrderDetailResponse` y `ProductRef`.

#### [MODIFY] [ApiService.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/data/network/ApiService.kt)
- Añadir `getOrdersByCliente(clienteId: Long): List<OrderResponse>` (`GET /pos-venta-service/order/cliente/{clienteId}`).

### ViewModel (`shared/src/commonMain/kotlin/pe/edu/upeu/presentation/viewmodel/`)

#### [MODIFY] [AppViewModel.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/viewmodel/AppViewModel.kt)
- Actualizar `confirmOrder` para tomar y enviar `customerName`, `customerDocument` y `customerAddress`.
- Añadir `fetchOrdersForCliente(clienteId: Long)` para consultar los pedidos del servidor y separar activos vs historial.

### Presentation (`shared/src/commonMain/kotlin/pe/edu/upeu/presentation/`)

#### [MODIFY] [TiendaScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/TiendaScreen.kt)
- Añadir Banner/Tarjeta de Pedido Activo en la cabecera cuando el cliente tenga órdenes en estado `EN_PREPARACION` o `EN_CAMINO`.

#### [MODIFY] [MisPedidosScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/MisPedidosScreen.kt)
- Implementar selector de Pestañas ("En Curso" vs "Historial").
- Renderizar tarjetas enriquecidas con badges y mensajes personalizados por estado.

#### [MODIFY] [CustomerMainContent.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/CustomerMainContent.kt)
- Pasar los datos del cliente logueado a `confirmOrder` e invocar la sincronización de pedidos al cambiar de pestaña.

## Verification Plan

### Automated Tests
- Ejecutar `gradle_build` (`assembleDebug`) para validar compilación exitosa en Kotlin Multiplatform.
