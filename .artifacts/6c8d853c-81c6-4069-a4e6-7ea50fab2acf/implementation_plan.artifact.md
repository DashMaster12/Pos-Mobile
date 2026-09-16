# Plan de Implementación: Control Global de Sesión y Restricciones de Vistas

Implementación de un estado de sesión global/compartido (`isLoggedIn`) en `CustomerMainContent` que se propaga a las pantallas de Tienda, Carrito, Mis Pedidos y Usuario para restringir o permitir funcionalidades interactivas (agregar al carrito, ver pedidos, etc.) según el estado de autenticación simulada.

## User Review Required

> [!IMPORTANT]
> - **Estado Global de Sesión**: Se eleva `isLoggedIn` (`true` por defecto) a `CustomerMainContent` para compartirlo con todas las pestañas y el carrito.
> - **Restricción en Tienda**: Si `isLoggedIn == false`, al intentar agregar un producto se muestra un diálogo de advertencia ("Debes iniciar sesión para agregar productos").
> - **Restricción en Carrito**: Si `isLoggedIn == false`, se muestra una vista vacía o aviso en el carrito deshabilitando la compra.
> - **Restricción en Mis Pedidos**: Si `isLoggedIn == false`, se oculta el historial de pedidos mostrando un estado vacío con invitación a iniciar sesión.
> - **Pantalla de Usuario**: Sincroniza el cierre de sesión y permite reactivarla interactivamente.

## Proposed Changes

### Presentation Screens & Content (`shared/src/commonMain/kotlin/pe/edu/upeu/presentation/`)

#### [MODIFY] [CustomerMainContent.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/CustomerMainContent.kt)
- Definir y elevar `isLoggedIn` a nivel de contenedor principal.
- Pasar `isLoggedIn` a `TiendaScreen`, `MisPedidosScreen`, `CartScreen` y `UsuarioScreen`.
- Gestionar diálogo de aviso cuando el usuario invitado intenta agregar al carrito.

#### [MODIFY] [TiendaScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/TiendaScreen.kt)
- Recibir parámetro `isLoggedIn` (o callback de intento bloqueado) para notificar al usuario invitado.

#### [MODIFY] [CartScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/CartScreen.kt)
- Recibir `isLoggedIn`. Si es `false`, mostrar estado requiriendo inicio de sesión y deshabilitar botón de confirmar pedido.

#### [MODIFY] [MisPedidosScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/MisPedidosScreen.kt)
- Recibir `isLoggedIn`. Si es `false`, ocultar historial y mostrar mensaje *"Inicia sesión para ver tu historial de pedidos"*.

#### [MODIFY] [UsuarioScreen.kt](file:///C:/Users/Markus/Documents/appmoviles/Pos-Mobile-main/shared/src/commonMain/kotlin/pe/edu/upeu/presentation/screens/UsuarioScreen.kt)
- Sincronizar el estado de sesión compartido con el botón de "Cerrar sesión" e "Iniciar sesión".

## Verification Plan

### Automated Tests
- Ejecutar `gradle_build` (`assembleDebug`) para validar compilación exitosa en Kotlin Multiplatform.
