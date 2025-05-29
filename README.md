# enSeñas: Aplicación Móvil para el Aprendizaje de la Lengua de Signos Española (LSE)

![Logo de la aplicación enSeñas - Puedes insertar una imagen aquí si la tienes]

## Descripción General

**enSeñas** es una aplicación móvil diseñada con el propósito fundamental de facilitar y promover el aprendizaje de la Lengua de Signos Española (LSE)[cite: 175]. Surge de la identificación de una carencia en el mercado de herramientas didácticas modernas y completas para este fin, donde la mayoría de las soluciones existentes se limitan a ser diccionarios o están desactualizadas[cite: 190].

Nuestra propuesta se centra en un enfoque pedagógico estructurado[cite: 193], ofreciendo un currículo de aprendizaje progresivo a través de lecciones organizadas, videos explicativos de alta calidad y evaluaciones interactivas. El objetivo principal de **enSeñas** es fomentar un aprendizaje accesible e inclusivo, promoviendo la comunicación y la integración social para personas sordas, con discapacidad auditiva, sus allegados y cualquier persona interesada en adquirir esta valiosa forma de comunicación[cite: 195, 196].

## Características Principales

* **Registro e Inicio de Sesión Seguro:** Permite a los usuarios crear y acceder a sus cuentas de forma segura[cite: 204, 205].
* **Interfaz Intuitiva y Atractiva:** Diseñada para ser sencilla de usar y visualmente moderna[cite: 206].
* **Listado de Lecciones Estructurado:** Lecciones organizadas por niveles y con bloqueo de progreso basado en evaluaciones[cite: 207, 209].
* **Visualización de Videos de Signos:** Reproducción de videos de alta calidad en cada lección y tema[cite: 208].
* **Evaluaciones Interactivas:** Tests al final de cada lección para evaluar los conocimientos y permitir el avance[cite: 209].
* **Seguimiento de Progreso y Logros:** Guarda el progreso del usuario y registra las lecciones completadas[cite: 210].
* **Personalización de Avatar:** Permite al usuario seleccionar y cambiar su foto de perfil (avatar) desde una galería predefinida[cite: 209].

## Tecnologías Utilizadas

* **Lenguaje de Programación:** Kotlin [cite: 262]
* **Entorno de Desarrollo (IDE):** Android Studio [cite: 264]
* **Backend como Servicio (BaaS):** Firebase [cite: 266]
    * **Firebase Authentication:** Para la gestión de usuarios (registro e inicio de sesión).
    * **Firebase Realtime Database:** Utilizada para almacenar la estructura de lecciones, tests y el progreso de los usuarios[cite: 266, 267].
    * **Cloud Storage for Firebase:** Para el alojamiento eficiente y escalable de los videos de signos[cite: 269].
* **Herramientas de Diseño:** Figma (para wireframes y prototipos)[cite: 260].
* **Herramientas de Diagramación:** Draw.io (para diagramas UML y E/R)[cite: 261], ClickUp (para diagrama de Gantt)[cite: 261].

## Instalación

La aplicación **enSeñas** se distribuye como un archivo APK directamente desde este repositorio de GitHub. Para instalarla en tu dispositivo Android, sigue estos pasos:

1.  **Habilitar la instalación de aplicaciones de fuentes desconocidas:**
    * Ve a `Ajustes` > `Seguridad` > `Instalar aplicaciones desconocidas` (o `Fuentes desconocidas` en versiones antiguas de Android).
    * Concede permiso a tu navegador web o gestor de archivos para instalar APKs.
2.  **Descargar el archivo APK:**
    * Accede a la sección de [Releases](https://github.com/tu_usuario/tu_repositorio/releases) de este repositorio o busca el archivo `ensenyas.apk` directamente en los archivos del proyecto.
    * Descarga el archivo APK a tu dispositivo.
3.  **Instalar la aplicación:**
    * Localiza el archivo `ensenyas.apk` descargado (normalmente en la carpeta "Descargas").
    * Pulsa sobre el archivo para iniciar la instalación y sigue las instrucciones en pantalla.

## Uso de la Aplicación

1.  **Abrir la aplicación.**
2.  **Registrarse o Iniciar Sesión:** Si eres un usuario nuevo, crea una cuenta. Si ya tienes una, inicia sesión con tus credenciales.
3.  **Navegar por Lecciones:** Explora la lista de lecciones organizadas por niveles.
4.  **Aprender Temas:** Accede a cada lección para ver los temas individuales y sus videos explicativos.
5.  **Realizar Tests:** Completa los tests al final de cada lección para evaluar tu aprendizaje y desbloquear contenido nuevo.
6.  **Gestionar Perfil:** Desde la pantalla de perfil, puedes ver tu progreso y personalizar tu avatar.

## Estructura del Proyecto

El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** para una organización clara y modular del código[cite: 271]:

* `model/`: Contiene las clases de datos (ej., `Leccion`, `Tema`, `Test`, `Usuario`).
* `ui/`: Alberga los componentes de la interfaz de usuario (pantallas).
    * `ui/auth/`: Pantallas relacionadas con autenticación, registro, selección de avatar y perfil de usuario.
    * `ui/common/`: Vistas y componentes reutilizables (ej. reproductor de video).
    * `ui/lessons/`: Pantallas de lista de lecciones, detalles de lección, detalles de tema y quiz.
    * `ui/theme/`: Definiciones de colores, temas y tipografía.
* `viewmodel/`: Contiene la lógica de negocio y gestión del estado de la interfaz (ej., `AuthViewModel`, `LessonViewModel`, `QuizViewModel`, `UserProfileViewModel`).
* `MainActivity.kt`: Punto de entrada principal de la aplicación y configuración de navegación.

## Contribución y Licencia

Este proyecto está bajo la licencia **GNU General Public License v3 (GPL v3)**[cite: 249]. Esta licencia garantiza a los usuarios la libertad de usar, modificar, compartir y distribuir el software[cite: 249], asegurando que todas las versiones y derivados también permanezcan libres y abiertos[cite: 250].

Se anima a la comunidad a contribuir con mejoras, nuevas funcionalidades y adaptaciones para seguir enriqueciendo la aplicación y fomentar la inclusión[cite: 255, 256].

* [Ver texto completo de la Licencia GPL v3](https://www.gnu.org/licenses/gpl-3.0.html) [cite: 259]
* [Ver traducción al español de la Licencia GPL v3](https://lslspanish.github.io/translation_GPLv3_to_spanish/) [cite: 259]

## Contacto / Autor

Para cualquier consulta o sugerencia, puedes contactar con:

* **Luis García Lizandra**
* Email: garcializandraluis@gmail.com [cite: 175]

---
