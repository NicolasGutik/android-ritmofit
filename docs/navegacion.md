# Cambios de Navegación

## 2025-09-21 - Integración de BottomNavigationView

- **Descripción**: Se agregó una barra de navegación inferior para permitir el acceso rápido a Clases (Home), Mis Reservas y Perfil.
- **Archivos creados**:
  - `app/src/main/res/menu/bottom_nav_menu.xml`
  - `app/src/main/res/drawable/ic_nav_home.xml`
  - `app/src/main/res/drawable/ic_nav_reservas.xml`
  - `app/src/main/res/drawable/ic_nav_perfil.xml`
  - `app/src/main/res/color/bottom_nav_color.xml`
- **Archivos modificados**:
  - `app/src/main/res/layout/activity_main.xml`
  - `app/src/main/java/com/ritmofit/app/MainActivity.java`
  - `gradle/libs.versions.toml`
  - `docs/cambios.md`

> Nota: La barra inferior se oculta automáticamente al navegar a pantallas que no forman parte del menú principal (por ejemplo, detalle de clase).

## 2025-09-22 - Barra inferior siempre visible

- **Descripción**: Se ajustó el comportamiento de la barra inferior para que permanezca visible en todas las pantallas del flujo principal, incluyendo los detalles de clase.
- **Archivos modificados**:
  - `app/src/main/java/com/ritmofit/app/MainActivity.java`
  - `docs/navegacion.md`

> Nota: El `BottomNavigationView` continúa sincronizado con el `NavController`, por lo que al volver desde pantallas secundarias conserva la pestaña activa correspondiente.
