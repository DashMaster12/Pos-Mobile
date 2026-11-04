# Tareas - Flujo de Pedidos, Seguimiento de Estados e Historial Segregado

- [x] **Fase 1: DTOs y ApiService**
  - [x] Actualizar `OrderDto.kt` (sin `orderType`, añadiendo campos del cliente y `kitchenNotes`)
  - [x] Añadir `getOrdersByCliente` en `ApiService.kt` (`GET /pos-venta-service/order/cliente/{clienteId}`)

- [x] **Fase 2: AppViewModel**
  - [x] Actualizar `confirmOrder` con los nuevos campos
  - [x] Implementar `fetchOrdersForCliente` para dividir pedidos activos vs historial

- [x] **Fase 3: Pantallas UI (TiendaScreen, MisPedidosScreen, CustomerMainContent)**
  - [x] Añadir Banner de Pedido Activo en la parte superior de `TiendaScreen.kt`
  - [x] Rediseñar `MisPedidosScreen.kt` con pestañas "En Curso" e "Historial"
  - [x] Conectar la sincronización de pedidos en `CustomerMainContent.kt`

- [x] **Fase 4: Verificación**
  - [x] Ejecutar `gradle_build` (`assembleDebug`) (Build finished successfully)
