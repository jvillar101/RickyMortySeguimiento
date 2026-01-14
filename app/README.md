# RICK Y MORTY SEGUIMIENTO

Aplicación Android nativa desarrollada en Kotlin para realizar el seguimiento de episodios de la serie "Rick y Morty".

Este proyecto combina el consumo de una API REST pública con una base de datos en la nube (Firebase) para ofrecer una experiencia personalizada, permitiendo a los usuarios marcar episodios como vistos y consultar estadísticas de progreso.

##  INTRODUCCIÓN

El propósito principal de esta aplicación es servir como una herramienta de "Check-list" inteligente.Esta app permite:
1.  Consultar la información oficial de los episodios.
2.  Persistir el estado (Visto/No visto) en la nube.
3.  Descubrir qué personajes aparecen en cada capítulo.


##  CARACTERÍSTICAS PRINCIPALES

### Autenticación y Perfil
- **Registro y Login:** Sistema seguro mediante Firebase Authentication (Email y Contraseña).
- **Sesión Persistente:** La app recuerda al usuario para no tener que loguearse cada vez.

###  Listado de Episodios
- **Carga Híbrida:** Combina datos estáticos de la API (nombres, fechas) con datos dinámicos de Firestore (estado de "visto").
- **Selección Múltiple:** Mediante una pulsación larga, se activa un modo de selección que permite marcar varios episodios como vistos simultáneamente.
- **Feedback Visual:** Los episodios vistos cambian su diseño (inversión de colores neón) para diferenciarse rápidamente.
- **Filtros:** Un Spinner permite alternar entre ver "Todos" los episodios o solo los "Vistos".

###  Detalles y Personajes
- **Info Extendida:** Al pulsar un episodio, se muestra el detalle con un interruptor (Switch) para marcarlo como visto individualmente.
- **Grid de Personajes:** La app extrae las URLs de los personajes del episodio y realiza una llamada inteligente a la API para mostrar sus fotos y nombres en una cuadrícula.

###  Estadísticas y Ajustes
- **Progreso:** Pantalla gráfica con una barra de progreso que calcula el porcentaje de la serie que ha completado el usuario.
- **Modo Oscuro:** Preferencia guardada localmente (SharedPreferences) para alternar el tema de la app.
- **Idioma:** Cambio de idioma.

##  TECNOLOGÍAS UTILIZADAS

El proyecto sigue una arquitectura **Single Activity** con **Navigation Component** en lenguaje **Kotlin**.

* **Retrofit:** Para el consumo de la [Rick and Morty API](https://rickandmortyapi.com/).
* **Gson:** Para el parseo de JSON.
* **Glide:** Para la carga y caché eficiente de imágenes (personajes).
* **Firebase Authentication:** Gestión de usuarios.
* **Cloud Firestore:** Base de datos NoSQL para guardar los IDs de episodios vistos (`users/{uid}/seen_episodes`).
* **RecyclerView:** Con `GridLayoutManager` y adaptadores personalizados.
* **Material Design:** `FloatingActionButton`, `SwitchMaterial`, `CardView`.
* **Navigation Drawer:** Menú lateral deslizante.
* **Almacenamiento Local:** `SharedPreferences` para guardar la configuración del tema (Modo Noche).

## CONCLUSIONES DEL DESARROLLADOR
Desarrollar esta aplicación ha supuesto un reto muy grande al tener que desarrollar la gestión de la API y la gestión de Firebase.

