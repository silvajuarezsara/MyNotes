# MyNotes — Proyecto base Android

Proyecto de referencia para la actividad de gestión de notas con Kotlin, Jetpack Compose, Room, SQLite, ViewModel, corrutinas, animaciones y gestos.

## Requisitos
- Android Studio compatible con Android Gradle Plugin 8.13.2.
- JDK 17.
- SDK Android 36.
- Conexión a Internet para descargar dependencias.

## Abrir
1. Descomprime el ZIP.
2. Abre la carpeta `MyNotes_Proyecto_Android` desde Android Studio.
3. Espera la sincronización de Gradle.
4. Ejecuta en un emulador o dispositivo Android (API 26 o superior).

## Funcionalidades incluidas
- Crear, consultar, editar y eliminar notas.
- Persistencia local mediante Room/SQLite.
- ViewModel + StateFlow + corrutinas.
- Marcar notas como completadas.
- Búsqueda por título y contenido.
- Animación de entrada/salida.
- Deslizar para eliminar con `SwipeToDismissBox`.
- Adjuntar una imagen con Photo Picker.
- Diseño adaptable: lista en pantallas compactas y cuadrícula en pantallas amplias.

## Pendientes didácticos
- Integrar un visor de imágenes a pantalla completa con pinch-to-zoom y límites de desplazamiento.
- Crear pruebas unitarias e instrumentadas.
- Añadir navegación formal si el equipo separa las pantallas.
- Preparar y firmar el APK desde Android Studio.
- Completar el prototipo en Figma y el video de demostración.
- Documentar commits, ramas y decisiones reales del equipo.

## Nota
Este es un proyecto base de trabajo, no una APK ya compilada ni una entrega final verificada. Revisa las versiones de Gradle/Kotlin/Compose si tu instalación de Android Studio utiliza otra configuración.
