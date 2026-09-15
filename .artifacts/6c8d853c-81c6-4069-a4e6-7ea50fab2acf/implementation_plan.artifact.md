# Plan de Implementación: Pantalla Completa de Carrito con Selector de Dirección

Transformación del carrito de compras de un panel flotante (`ModalBottomSheet`) a una **pantalla completa dedicada** accesible desde el icono de la barra superior de la tienda, con selector integrado de dirección de entrega y botón directo para confirmar pedido.

## User Review Required

> [!IMPORTANT]
> - El carrito deja de ser un BottomSheet y se convierte en una vista/pantalla completa dentro de `CustomerMainContent` (o gestionada mediante un estado de pantalla `currentScreen` / `showCartScreen`).
> - Se integra un selector de dirección de envío directamente en la vista del carrito.
> - Al confirmar el pedido, se genera el registro en "Mis Pedidos" y se redirige automáticamente a dicha pestaña.

## Proposed Changes

### Screens & Navigation (`shared/src/commonMain/kotlin/pe/edu/upeu/presentation/`)

#### [NEW] [screens/CartScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/CartScreen.kt)
- Pantalla completa de carrito de compras:
  - Lista de productos con cantidades, subtotales y opción de modificar/eliminar.
  - Sección de selección de dirección de envío (con selector de direcciones guardadas y opción de añadir nueva dirección rápida).
  - Resumen total de compra.
  - Botón inferior fijo "Confirmar Pedido" / "Realizar Pedido".

#### [MODIFY] [CustomerMainContent.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/CustomerMainContent.kt)
- Reemplazar `showCartSheet` por el estado de navegación a pantalla completa de carrito (`showCartScreen: Boolean`).
- Gestionar las direcciones disponibles y la dirección seleccionada para el envío.
- Manejar la lógica de confirmación de pedido que transfiere el carrito a `ordersList` y redirige a `DeliveryRoute.MisPedidos`.

## Verification Plan

### Automated Tests
- Ejecutar `gradle_build` (`assembleDebug`) para validar compilación exitosa en Kotlin Multiplatform.
