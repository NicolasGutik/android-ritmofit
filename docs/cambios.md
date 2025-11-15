# Registro de Cambios Recientes

## 2025-09-21
- **LoginActivity**: Se eliminó el relanzamiento incondicional de `MainActivity` para evitar el bucle infinito cuando el usuario no está autenticado.
- **Dependencias**: Se añadió la entrada `security-crypto` en `gradle/libs.versions.toml` para resolver la referencia `libs.security.crypto` utilizada en el módulo `app`.
- **Navegación principal**: Se integró un `BottomNavigationView` en `MainActivity` junto con su menú (`res/menu/bottom_nav_menu.xml`), íconos vectoriales y selector de color, conectándolo al `NavController` para navegar entre Home, Mis Reservas y Perfil.

## 2025-09-22
- **BottomNavigationView**: Se actualizó `MainActivity` para mantener la barra inferior visible en todas las pantallas, facilitando el acceso permanente a Clases, Mis Reservas y Perfil.
