# Walkthrough - Seguimiento de Estados de Pedido e Historial Segregado

He completado la implementación del flujo de pedidos según tu especificación exacta. La aplicación móvil ahora no envía `orderType`, consulta las órdenes del cliente en tiempo real (`GET /pos-venta-service/order/cliente/{clienteId}`), muestra un **Banner Destacado de Pedido Activo** en la pantalla principal (Home), y segrega los pedidos en dos pestañas claras en la pantalla **Mis Pedidos** ("En Curso" e "Historial").

## Cambios Realizados

### 1. DTOs y Red (`OrderDto.kt`, `ApiService.kt`)
- **Remoción de `orderType`**: Se eliminó del cuerpo de la petición.
- **Campos Enriquecidos**: `CreateOrderRequest` envía ahora `clienteId`, `customerName`, `customerDocument` y `customerAddress`.
- **Consulta de Pedidos por Cliente**: Implementada en `ApiService.kt` (`GET /pos-venta-service/order/cliente/{clienteId}`).

### 2. Lógica de Estados en ViewModel (`AppViewModel.kt`)
- Carga dinámica de los pedidos del cliente autenticado mediante `fetchOrdersForCliente(clienteId)`.
- **Filtro y Segregación**:
  - **Pedidos Activos**: `EN_PREPARACION` ("En preparación 🛵") y `EN_CAMINO` ("En camino 🚚").
  - **Historial de Pedidos**: `ENTREGADO` ("Entregado ✅") y `CANCELADO` ("Cancelado ❌").

### 3. Pantalla Principal / Home (`TiendaScreen.kt`)
- **Banner de Pedido Activo**: Si el cliente tiene un pedido en curso, aparece una tarjeta azul destacada en la parte superior del menú mostrando el número de orden y mensaje dinámico (*"🛵 Tu pedido se está preparando en cocina..."* o *"🚚 ¡El repartidor va en camino a tu dirección!"*).

### 4. Pantalla Mis Pedidos (`MisPedidosScreen.kt`)
- **Control por Pestañas**: Pestañas con contadores `"En Curso (N)"` e `"Historial (N)"`.
- **Refresco de Estado**: Botón de recarga para refrescar los estados dinámicos desde el servidor de Render.

---

## Resultados de Verificación

### Pruebas de Compilación
- Ejecutado `gradle_build` (`assembleDebug`):
  > [!NOTE]
  > **Build finished successfully.** El proyecto compila limpiamente en Kotlin Multiplatform.
