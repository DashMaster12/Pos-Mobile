# Resumen de Arquitectura y Propósito de la App ("Alé Burgers") para Backend (Supabase)

Este documento está diseñado para ser compartido con **Antigravity** en VS Code con el fin de estructurar el backend y la integración con **Supabase (Base de datos en la nube)** para la aplicación móvil **Alé Burgers**.

---

## 1. Propósito y Dominio de la Aplicación
**Alé Burgers** es una aplicación móvil B2C de **pedidos directos (tipo delivery de comida rápida)** desarrollada en **Kotlin Multiplatform (KMP)** con **Jetpack Compose (Material 3)**. Permite a los clientes finales:
- Explorar un menú de comida (hamburguesas, pizzas, alitas, etc.).
- Gestionar un carrito de compras y realizar el proceso de checkout eligiendo direcciones de envío.
- Ver el historial y el estado en tiempo real de sus pedidos activos (expandibles con desglose detallado).
- Gestionar su perfil de usuario, direcciones guardadas, métodos de pago y control de sesión (invitado vs. usuario autenticado).

---

## 2. Modelo de Datos Esperado en Supabase (Tablas Principales)

Para soportar todas las vistas y flujos actuales de la app móvil, el backend en Supabase requerirá las siguientes entidades/tablas principales:

### A. Tabla `users` (Perfiles de Usuario)
Extiende el sistema de autenticación de Supabase (`auth.users`).
- `id` (UUID, Primary Key, FK a `auth.users`)
- `full_name` (Text)
- `email` (Text)
- `phone` (Text)
- `created_at` (Timestamp)

### B. Tabla `products` (Menú / Catálogo de la Tienda)
- `id` (UUID o Int, Primary Key)
- `name` (Text) - Ej: "Hamburguesa Deluxe"
- `description` (Text) - Ej: "Doble carne, queso cheddar y tocino"
- `price` (Numeric) - Ej: 25.00
- `image_url` (Text, opcional para futuro)
- `category` (Text)
- `is_active` (Boolean)

### C. Tabla `addresses` (Direcciones de Envío del Usuario)
- `id` (UUID, Primary Key)
- `user_id` (UUID, FK a `users.id`)
- `address_text` (Text) - Ej: "Av. Principal 123, Lima"
- `is_default` (Boolean)

### D. Tabla `payment_methods` (Métodos de Pago del Usuario)
- `id` (UUID, Primary Key)
- `user_id` (UUID, FK a `users.id`)
- `method_name` (Text) - Ej: "Visa •••• 4242" o "Yape"

### E. Tabla `orders` (Pedidos Realizados)
- `id` (UUID, Primary Key)
- `user_id` (UUID, FK a `users.id`)
- `order_code` (Text) - Ej: "#1090"
- `status` (Text) - Ej: "En preparación 🛵", "En Camino 🛵", "Entregado ✅"
- `subtotal` (Numeric)
- `delivery_fee` (Numeric)
- `total` (Numeric)
- `delivery_address` (Text)
- `created_at` (Timestamp)

### F. Tabla `order_items` (Detalle de Productos por Pedido)
- `id` (UUID, Primary Key)
- `order_id` (UUID, FK a `orders.id`)
- `product_id` (UUID, FK a `products.id`)
- `quantity` (Int)
- `unit_price` (Numeric)

---

## 3. Estrategia de Trabajo con Supabase (Cloud Free Tier)
- **Base de Datos Virtual (Cloud)**: Se utilizará Supabase en la capa gratuita para evitar configuraciones locales en Docker o PostgreSQL local.
- **Autenticación**: Supabase Auth (Email/Password o Social Auth si se requiere).
- **Acceso a Datos**:
  - Desde el cliente KMP se puede consumir la SDK oficial de Supabase para Kotlin o mediante llamadas a una API intermedia (ej. Ktor en backend o Supabase REST/PostgREST directo).
  - Row Level Security (RLS) en Supabase para garantizar que cada usuario solo lea/escriba sus propias direcciones, carrito y pedidos.
