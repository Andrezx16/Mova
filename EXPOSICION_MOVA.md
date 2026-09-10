# MOVA — Guía de exposición (arquitectura y funcionamiento interno)

Este documento no describe "qué hace la app" desde el punto de vista del usuario — describe **cómo está construido el código** para que las 4 personas puedan explicarle al profesor, con seguridad, por qué cada archivo existe, qué problema resuelve y cómo se conecta con el resto.

---

## 0. Antes de dividir: cómo está armada la aplicación

MOVA es una app Android nativa escrita en **Kotlin** con **Jetpack Compose** (la UI se declara con funciones `@Composable`, no con XML). Sigue el patrón **MVVM** (Model-View-ViewModel) en 3 capas:

```
┌─────────────────────────────────────────────────────────────┐
│  UI (Composables)  ←── observa StateFlow ──  ViewModel       │
│  ui/home, ui/search, ui/auth, ui/tour, ui/listings...        │
└───────────────────────────┬───────────────────────────────────┘
                             │ llama funciones suspend / recibe Flow
┌───────────────────────────▼───────────────────────────────────┐
│  Repository (data/repository/*)                                │
│  Traduce entre el modelo de Kotlin y Firestore/Firebase Auth   │
└───────────────────────────┬───────────────────────────────────┘
                             │ SDK de Firebase / Cloudinary
┌───────────────────────────▼───────────────────────────────────┐
│  Servicios externos: Firebase Auth, Cloud Firestore, Cloudinary│
└─────────────────────────────────────────────────────────────┘
```

Piezas transversales que **todas** las pantallas usan:

- **Hilt** (inyección de dependencias): marca clases con `@Inject`/`@HiltViewModel`/`@Module` y Hilt construye automáticamente el árbol de objetos (ViewModel → Repository → FirebaseAuth/Firestore) sin que nadie escriba `new` a mano.
- **StateFlow**: cada ViewModel expone su estado como `StateFlow` (una variable observable). La UI lo lee con `val estado by viewModel.algo.collectAsState()` y Compose **recompone** (redibuja) automáticamente esa parte de la pantalla cuando el valor cambia. Este patrón se repite en casi todos los archivos — entenderlo bien es la clave para explicar cualquier pantalla.
- **Coroutines** (`viewModelScope.launch { ... }`, `suspend fun`, `.await()`): todas las llamadas a Firebase son asíncronas; las coroutines evitan bloquear la UI mientras se espera la respuesta de la red.
- **Navigation Compose** (`NavHost`/`Screen`): una sola Activity (`MainActivity`) contiene todas las pantallas; navegar es simplemente decirle al `NavController` "cambia de ruta", no abrir una Activity nueva.

División del trabajo (56 archivos Kotlin en total, repartidos según la arquitectura real, no en partes iguales arbitrarias):

| # | Persona | Área | Archivos |
|---|---------|------|----------|
| 1 | Persona 1 | Datos, servicios y arranque de la app | 16 |
| 2 | Persona 2 | Autenticación, navegación y sistema de diseño | 10 |
| 3 | Persona 3 | Descubrimiento de propiedades (Home/Búsqueda/Guardados/Detalle) | 19 |
| 4 | Persona 4 | Tours, publicaciones (listings) y perfil | 11 |

Cada parte se explica de forma que la persona pueda hacer un recorrido "de principio a fin" de su porción sin depender de memorizar las otras — pero al final hay una sección de **cómo se conectan las 4 partes**, que todos deberían leer para responder preguntas cruzadas del profesor.

---

## 1. Persona 1 — Arquitectura, datos y servicios (el "backend" del cliente)

### Idea central que debes poder explicar
"La app nunca guarda datos en variables sueltas: todo pasa por un Repository, que es el único que conoce Firebase/Cloudinary. Los ViewModels y la UI no saben qué es Firestore — solo hablan con el Repository."

### Archivos a estudiar

**Modelos de datos — `data/model/`**
- `Property.kt` — representa un inmueble. Tiene `toMap()` y `fromMap()`: Firestore no guarda objetos Kotlin, guarda documentos JSON (`Map<String, Any>`), así que cada modelo sabe convertirse a mapa y reconstruirse desde un mapa. Fíjate en los campos `ownerId` e `isActive`: son los que permiten que un usuario "publique" una propiedad y la pause sin borrarla.
- `UserProfile.kt` — perfil de usuario (nombre, foto, bio, `membershipStatus`). También con `toMap()`/`fromMap()`.
- `TourBooking.kt` — una solicitud de visita a una propiedad. Tiene un campo `status` con 5 valores posibles definidos como constantes: `STATUS_PENDING`, `STATUS_CONFIRMED`, `STATUS_CANCELLED`, `STATUS_COMPLETED`, `STATUS_REJECTED`. Este es literalmente una **máquina de estados** — explica el ciclo de vida (lo usará también la Persona 4).
- `PropertyCategory.kt` — enum simple (`ALL, HOUSE, VILLA, APARTMENT, PENTHOUSE, TOWNHOUSE`), cada valor apunta a un string traducible (`@StringRes`).
- `HomeNavTab.kt` — enum para las 4 pestañas de la barra inferior (Home/Search/Saved/Profile), cada una con su ícono seleccionado/no seleccionado.

**Repositorios — `data/repository/`** (el corazón de tu parte)
- `AuthRepository.kt` — envuelve `FirebaseAuth`. `signUp()` crea el usuario en Firebase Auth **y** su documento en la colección `users` de Firestore en la misma operación (si no lo hiciera, existiría el login pero no el perfil). `signIn()` además intenta cargar el perfil, y si no existe lo crea (caso de login con Google, por ejemplo). Explica por qué usa `Result<T>` como tipo de retorno (`Result.success` / `Result.failure`) en vez de lanzar excepciones: así el ViewModel decide qué mostrar sin `try/catch`.
- `PropertyRepository.kt` — el más importante para entender Firestore en tiempo real. Métodos como `getFeaturedProperties()` devuelven un `Flow<List<Property>>` construido con `callbackFlow` + `addSnapshotListener`: eso significa que **no es una sola consulta**, es una suscripción — si otro usuario agrega una propiedad, este Flow emite una nueva lista automáticamente sin recargar la pantalla. Nota el parámetro `excludedOwnerId`: filtra (en el cliente, con `.filter { }`, no en la consulta) las propiedades del propio usuario para que un dueño no vea sus publicaciones mezcladas en el feed público. También tiene `hasAnyProperties()` (usado para el auto-seed, ver `FirestoreSeeder`) y CRUD normal (`addProperty`, `updateProperty`, `deleteProperty`, `setPropertyActive`) con funciones `suspend` (una sola vez, no en tiempo real).
- `SavedRepository.kt` — favoritos. Ojo con el modelo de datos: no hay una colección "favoritos por propiedad", hay **un documento por usuario** en la colección `favorites` con un array `propertyIds`. `toggleFavorite()` usa `FieldValue.arrayUnion`/`arrayRemove` de Firestore para agregar/quitar del array de forma atómica.
- `TourRepository.kt` — CRUD de `TourBooking`. Tiene dos consultas simétricas: `getUserTours(userId)` (lo que ve el visitante) y `getOwnerTours(ownerId)` (lo que ve el dueño de la propiedad) — ambas leen la **misma colección** `tours`, solo cambia el filtro (`whereEqualTo("userId", ...)` vs `whereEqualTo("propertyOwnerId", ...)`). `updateTourStatus()` es privado y lo reutilizan `cancelTour`, `completeTour` y `respondToTour` — evita repetir código.
- `UserRepository.kt` — CRUD del perfil (nombre, teléfono, bio, avatar) con updates parciales (`update("campo", valor)` en vez de reescribir todo el documento).

**Inyección de dependencias y arranque**
- `di/FirebaseModule.kt` — un `@Module` de Hilt: le dice a Hilt "cuando alguien pida un `FirebaseAuth` o un `FirebaseFirestore`, dame `FirebaseAuth.getInstance()`". Sin este archivo, ningún `@Inject constructor(private val auth: FirebaseAuth)` del resto de la app compilaría.
- `LuxeRealtyApp.kt` — la `Application` de Android, marcada con `@HiltAndroidApp`. Esa anotación es la que activa todo el sistema de Hilt en tiempo de compilación (genera el "grafo" de dependencias). Es intencionalmente mínima.
- `MainActivity.kt` — la única Activity. Marcada `@AndroidEntryPoint` (para que Hilt pueda inyectar aquí también) y su `onCreate()` solo hace `setContent { Project304Theme { AppNavHost() } }` — todo lo demás vive en Compose, no en la Activity.

**Utilidades — `util/`**
- `CloudinaryManager.kt` — sube imágenes (fotos de propiedades, avatares) a Cloudinary usando un **unsigned upload preset**. Explica la decisión de seguridad: Cloudinary también soporta subir con `API key + secret`, pero eso obligaría a guardar el secret dentro del APK, y cualquiera podría extraerlo descompilando la app. Con un preset "unsigned" configurado en el panel de Cloudinary, el cliente solo necesita el nombre de la cuenta (`cloud_name`) y el nombre del preset — nunca el secret.
- `FirestoreSeeder.kt` — contiene una lista fija de propiedades de ejemplo y las escribe en Firestore. Se llama automáticamente **una sola vez**, desde `HomeViewModel`, solo si `PropertyRepository.hasAnyProperties()` devuelve `false` (para no duplicar datos en cada apertura de la app).
- `LocaleHelper.kt` — cambia el idioma de la app en tiempo de ejecución (`Locale`, `Configuration`, `createConfigurationContext`) y lo persiste en `SharedPreferences`. Si el usuario nunca eligió idioma, por defecto es español (`DEFAULT_LANGUAGE = "es"`).

### Cómo se conecta con las demás partes
Cada ViewModel que verán las Personas 2, 3 y 4 recibe uno o varios de estos repositorios por constructor (`@Inject constructor(private val propertyRepository: PropertyRepository, ...)`). Si te preguntan "¿de dónde saca los datos la pantalla X?", la respuesta siempre pasa por aquí.

### Preguntas típicas que te puede hacer el profesor
- ¿Por qué `Flow`/`callbackFlow` en vez de simplemente pedir los datos una vez? → tiempo real, la UI se actualiza sola.
- ¿Qué pasaría si dos repositorios necesitaran la misma instancia de `FirebaseFirestore`? → Hilt la provee como `@Singleton`, es la misma instancia en toda la app.
- ¿Por qué no se sube el API secret de Cloudinary? → riesgo de seguridad al descompilar el APK.

---

## 2. Persona 2 — Autenticación, navegación y sistema de diseño

### Idea central que debes poder explicar
"Toda la app vive dentro de un único `NavHost`. Qué pantalla se ve depende de una 'ruta' (String), y esa ruta cambia según si Firebase dice que hay sesión iniciada o no. Además, todas las pantallas comparten una misma paleta de colores y tipografía definida en un solo lugar."

### Archivos a estudiar

**Autenticación — `ui/auth/`**
- `AuthViewModel.kt` — expone un solo `StateFlow<AuthUiState>`. `AuthUiState` es una **sealed class** con 4 posibilidades: `Idle`, `Loading`, `Success`, `Error(mensaje)`. Este patrón (un sealed class que representa "en qué estado visual está la pantalla") se repite conceptualmente en casi todas las pantallas de la app, así que dominarlo aquí te sirve para explicar cualquier otra. `signIn()`/`signUp()` simplemente llaman a `AuthRepository` y traducen el `Result` en uno de esos 4 estados.
- `LoginScreen.kt` / `SignUpScreen.kt` — usan `val uiState by viewModel.uiState.collectAsState()` y un `LaunchedEffect(uiState)` que navega (`onLoginSuccess()`) en cuanto el estado es `Success`. Explica la diferencia entre `viewModel.resetState()` (se llama cuando el usuario vuelve a escribir después de un error, para que el mensaje de error desaparezca) y el flujo normal.

**Navegación — `ui/navigation/`**
- `Screen.kt` — una `sealed class` donde cada pantalla es un objeto con su `route` (String). Las rutas con parámetros (`PropertyDetail`, `ScheduleTour`, `Tours`, `ListingForm`) tienen una función `createRoute(id)` que arma el string (`"property_detail/$id"`) para no escribirlo a mano en cada sitio y arriesgarse a un typo.
- `NavHost.kt` — el mapa completo de la app. Dos cosas clave:
  1. `startDestination` se calcula **antes** de dibujar nada: `if (FirebaseAuth.getInstance().currentUser != null) Screen.Home.route else Screen.Login.route`. Así, si cierras la app con sesión iniciada y la vuelves a abrir, no te vuelve a pedir login.
  2. Cada `composable(route = ...) { }` conecta una pantalla con sus callbacks de navegación (`onNavigateToX = { navController.navigate(...) }`). Cuando el usuario cierra sesión, se usa `popUpTo(0) { inclusive = true }` para borrar **todo** el historial de navegación — así no puede volver atrás a una pantalla que requería estar logueado.

**Sistema de diseño — `ui/theme/`** (todas las pantallas dependen de esto)
- `Color.kt` — todos los colores de la app como constantes (`Cream`, `SageGreen`, `DeepBrown`, `WarmTaupe`, `PaleSage` = la paleta "WoodNest"; más `GlassSurfaceLight/Dark`, `GlassBorder`, `GlassShadow` para el efecto vidrio). Ningún archivo de UI escribe un color hexadecimal suelto (salvo casos puntuales) — todos importan de aquí, así que un cambio de marca se hace en un solo archivo.
- `Type.kt` — define dos familias tipográficas cargadas desde `res/font/`: **Anton** (para títulos grandes/precios) y **Quicksand** (para el resto del texto). Quicksand es una *variable font* (un solo archivo `.ttf` con un eje de "peso"); se registran 4 pesos distintos (`Normal/Medium/SemiBold/Bold`) usando `FontVariation.Settings(FontVariation.weight(...))` — vale la pena entender qué es una variable font porque es una decisión técnica poco común. También redefine el `Typography` de Material3 global, así que **toda** la app heredó esta tipografía sin tener que tocar cada pantalla una por una.
- `Theme.kt` — `Project304Theme(...)`: arma el `ColorScheme` de Material3 (claro/oscuro) y envuelve el contenido en `MaterialTheme(...)`. Es lo primero que se llama en `MainActivity`.
- `NatureImagery.kt` — solo 3 constantes con URLs de fotos (Unsplash) usadas como fondo en Login/Home.

**Componentes de diseño — `ui/components/GlassComponents.kt`**
- `GlassSurface` — la "tarjeta de vidrio" reutilizable: fondo semitransparente + sombra + borde de 1dp. Aquí va la aclaración técnica más importante de tu parte: **no es un blur real**. Un desenfoque real de lo que hay detrás (glassmorphism auténtico) requiere `RenderEffect`, disponible solo desde Android 12 (API 31), y esta app soporta desde Android 7 (`minSdk = 24`). Por eso el "vidrio" se simula con transparencia + borde + sombra en vez de un blur de verdad — es una decisión consciente, no una limitación no explicada.
- `NatureBackdrop` — una foto de fondo (`AsyncImage` de Coil) con un degradado oscuro encima para que el texto blanco se lea.
- `GlassPrimaryButton` — el botón principal (crema con texto café).
- `MovaLogo` — un solo composable con un enum `MovaLogoVariant.MARK/WORDMARK` que decide qué imagen del logo mostrar (la "M" sola para la barra superior, el logotipo completo para Login/SignUp).

### Cómo se conecta con las demás partes
Absolutamente todas las pantallas de las Personas 3 y 4 importan colores de `Color.kt`/`Type.kt` y, varias, usan `GlassSurface`/`NatureBackdrop`/`MovaLogo`. `NavHost` es quien "llama" a las pantallas de todos — si te preguntan "¿cómo se llega a la pantalla de detalle de propiedad?", la respuesta está en tu `NavHost.kt`.

### Preguntas típicas
- ¿Qué es una `sealed class` y por qué se usa para `Screen` y `AuthUiState`? → representa un conjunto **cerrado** de posibilidades; el compilador te obliga a manejar todos los casos en un `when`.
- ¿Por qué el "vidrio" no es un blur real? → limitación de `minSdk 24` vs la API de blur (24 → 31).
- ¿Qué pasa si el usuario cierra sesión y presiona "atrás"? → no puede volver, por el `popUpTo(0)`.

---

## 3. Persona 3 — Descubrimiento de propiedades (Home, Búsqueda, Guardados, Detalle)

### Idea central que debes poder explicar
"Esta es la parte más grande porque es donde el usuario pasa más tiempo: ver, buscar, filtrar, guardar y abrir el detalle de una propiedad. Cuatro pantallas distintas, pero todas siguen el mismo patrón ViewModel + StateFlow + Repository, y comparten las mismas tarjetas y barras visuales."

### Archivos a estudiar

**Home — `ui/home/`**
- `HomeUiState.kt` — una sola `data class` con **todo** el estado de la pantalla (query de búsqueda, categoría elegida, listas de propiedades, IDs favoritos, pestaña activa, etc.). Tiene propiedades computadas (`filteredFeaturedProperties`, `filteredRecommendedProperties`) que aplican el filtro de categoría/búsqueda **sin volver a pedir datos a Firestore** — se recalculan al vuelo sobre lo que ya está en memoria.
- `HomeViewModel.kt` — el ViewModel más "orquestador" de la app: en su `init {}` (1) intenta sembrar la base de datos si está vacía (`FirestoreSeeder`, ver Persona 1), (2) se suscribe en paralelo a `getFeaturedProperties()` y `getRecommendedProperties()` de `PropertyRepository`, y (3) se suscribe a `getFavoriteIds()` de `SavedRepository`. Tres flujos independientes que van actualizando el mismo `HomeUiState` con `_uiState.update { it.copy(...) }`.
- `HomeScreen.kt` — el `@Composable`. Nota cómo NO tiene lógica de negocio: solo lee `uiState`, dibuja, y cuando el usuario toca algo llama a una función del ViewModel (`viewModel.onSearchQueryChange(it)`, `viewModel.onFavoriteToggle(id)`). Este es el principio de MVVM: la Vista es "tonta", toda la decisión vive en el ViewModel.

**Búsqueda — `ui/search/`**
- `SearchViewModel.kt` — más simple que `HomeViewModel`: expone `properties` (todas, vía `getAllProperties()`) y `favoriteIds`, y el filtrado (por texto, tipo, precio, habitaciones, comodidades) se hace **dentro del Composable** (`SearchPropertiesScreen.kt`) con funciones Kotlin normales (`.filter { }`), no en el ViewModel. Vale la pena poder explicar por qué (es estado de UI puramente local — no necesita sobrevivir a rotación de forma persistente, ni otras pantallas lo necesitan).
- `SearchPropertiesScreen.kt` — pantalla grande con estados: inicial (antes de aplicar filtros), sin resultados, y con resultados paginados (`itemsPerPage`). Usa `FilterBottomSheet`-style controles inline (chips, `RangeSlider` de precio, checkboxes de comodidades).
- `SearchResultCard.kt` — la tarjeta de cada resultado.

**Guardados — `ui/saved/`**
- `SavedPropertiesViewModel.kt` — patrón interesante: se suscribe a `getFavoriteIds(userId)` de `SavedRepository`, y **por cada ID** pide la propiedad completa a `PropertyRepository.getPropertyById()`. Es decir, combina dos repositorios distintos para armar una sola lista.
- `SavedPropertiesScreen.kt` / `SavedPropertyCard.kt` — lista con opciones de ordenar (reciente/precio) y botón para quitar de favoritos o agendar tour.

**Detalle — `ui/details/`**
- `PropertyDetailViewModel.kt` — el más simple de estudiar: cuando la pantalla pide `loadProperty(id)`, hace **una sola** consulta `suspend` (no un `Flow` en tiempo real, porque no hace falta que el detalle se actualice solo) y separadamente consulta si esa propiedad ya es favorita del usuario actual.
- `PropertyDetailScreen.kt` — mientras `property` es `null` (todavía cargando) muestra un `CircularProgressIndicator`; en cuanto llega el dato, dibuja toda la pantalla. Este patrón ("pantalla de carga mientras el StateFlow es null") es un buen ejemplo para explicar estados de carga.

**Componentes compartidos — `ui/components/`** (los usan Home, Búsqueda, Guardados y a veces Detalle)
- `LuxeTopBar.kt` — barra superior: solo el logo centrado (se simplificó a pedido — antes tenía botones de menú/perfil sin funcionalidad real y se retiraron).
- `LuxeBottomNavBar.kt` — las 4 pestañas. Presta atención al detalle de diseño: la pestaña activa envuelve **ícono + texto juntos** en una sola píldora verde con texto café oscuro (para que haya contraste); las inactivas quedan claras sobre el fondo oscuro de la barra.
- `LuxeSearchBar.kt`, `CategoryChipsRow.kt`, `FilterBottomSheet.kt` — controles de búsqueda/filtro reutilizados en más de una pantalla.
- `FeaturedPropertyCard.kt` / `RecommendedPropertyCard.kt` — dos estilos distintos de tarjeta de propiedad (la de "destacada" es una foto grande con degradado; la de "recomendada" es una tarjeta con foto arriba y datos abajo).
- `PropertyImage.kt` — un componente muy pequeño pero importante: decide si mostrar la foto real (`AsyncImage` de Coil, cargando la URL de Cloudinary/Firestore) o, si la propiedad no tiene foto, un ícono de placeholder. Todas las tarjetas de propiedad de toda la app pasan por aquí — así se evita repetir la misma lógica en cada tarjeta.

### Cómo se conecta con las demás partes
Usa constantemente `PropertyRepository` y `SavedRepository` (Persona 1), los colores/fuentes/`GlassSurface` (Persona 2), y navega hacia el Detalle, hacia Agendar Tour y hacia Perfil (pantallas de la Persona 4) a través de callbacks que recibe desde `NavHost` (Persona 2).

### Preguntas típicas
- ¿Por qué Home tiene 3 suscripciones (`Flow`) separadas en vez de una sola? → cada una viene de una colección/documento distinto de Firestore (properties destacadas, properties recomendadas, favoritos del usuario).
- ¿Por qué el detalle de propiedad no usa `Flow` como Home? → no necesita tiempo real, es una consulta puntual.
- ¿Qué pasa si `PropertyImage` no tiene ni URL ni recurso? → muestra un ícono de casa como placeholder, nunca deja el espacio vacío o rompe el layout.

---

## 4. Persona 4 — Tours, publicaciones (listings) y perfil

### Idea central que debes poder explicar
"Esta parte cubre el lado 'dueño de propiedad' de la app: publicar inmuebles, recibir y responder solicitudes de visita, y administrar el perfil propio. Es la parte con más reglas de negocio (estados, validaciones, permisos)."

### Archivos a estudiar

**Reserva y gestión de tours — `ui/tour/`**
- `ScheduleTourViewModel.kt` / `ScheduleTourScreen.kt` — agenda una visita. Antes de reservar, valida que el usuario no sea el dueño de la propiedad (`_property.value?.ownerId == currentUserId` → error "no puedes agendar en tu propia publicación"). Al confirmar, crea un `TourBooking` con `status = STATUS_PENDING` vía `TourRepository`.
- `ToursViewModel.kt` / `ToursScreen.kt` — lista "Mis Tours" (lado visitante). Un mismo ViewModel sirve **dos** pantallas lógicas (activos e historial) gracias al parámetro `showHistory: Boolean`: filtra la misma lista de `tours` según si el estado es "activo" (`PENDING`/`CONFIRMED`) o "histórico" (`CANCELLED`/`COMPLETED`/`REJECTED`). Tiene acciones `cancelTour`, `completeTour`, `deleteTour` y `clearHistory` (borra en lote con `TourRepository.deleteTours()`, que usa un **batch write** de Firestore para borrar varios documentos en una sola operación atómica).

**Publicaciones (listings) — `ui/listings/`**
- `ListingsViewModel.kt` — el ViewModel con más lógica de negocio de tu parte. `saveProperty(...)` hace, en orden: (1) valida que todos los campos numéricos sean válidos, (2) si hay una foto nueva la sube primero a Cloudinary (`CloudinaryManager.uploadPropertyImage`) y **solo cuando esa subida termina** (callback `::persist`) arma el objeto `Property` y lo guarda en Firestore — es un buen ejemplo de callback anidado dentro de una función que también usa coroutines. También expone `respond(tourId, accepted)`: así es como el **dueño** acepta o rechaza una solicitud de tour (cambia el `status` a `CONFIRMED` o `REJECTED`).
- `ListingsScreen.kt` — "Mis publicaciones": lista las propiedades del usuario (`ListingsViewModel.properties`, que viene de `PropertyRepository.getUserProperties()`) con botones Editar / Pausar-Activar / Eliminar, y un botón que lleva a las solicitudes recibidas mostrando cuántas están pendientes (`requests.count { it.status == "PENDING" }`).
- `ListingFormScreen.kt` — formulario para crear/editar una propiedad (mismo formulario sirve para ambos casos, decide según si `propertyId == "new"`). Usa `rememberLauncherForActivityResult(ActivityResultContracts.GetContent())` para abrir el selector de fotos del sistema.
- `ReceivedToursScreen.kt` — lista de solicitudes de tour que le llegan al dueño de una propiedad, con botones Aceptar/Rechazar que llaman a `ListingsViewModel.respond()`.

**Perfil — `ui/profile/`**
- `UserProfileViewModel.kt` — carga el perfil (`UserRepository`), y también el conteo de guardados (`SavedRepository`) y de tours (`TourRepository`) para mostrarlos como estadísticas. `uploadAvatar(uri)` reutiliza el mismo `CloudinaryManager` que usan las publicaciones, pero con la carpeta `avatars` en vez de `properties` — mismo mecanismo, distinto destino.
- `UserProfileScreen.kt` — muestra el perfil, permite tocar la foto para cambiarla, y tiene el botón de **Cerrar sesión**, que llama a `AuthRepository.signOut()` y dispara `onSignOut()` — ese callback, definido en `NavHost` (Persona 2), es el que hace `popUpTo(0)` para limpiar el historial.
- `AboutScreen.kt` — pantalla estática (sin ViewModel) con el logo, una descripción corta de la app y los nombres del equipo. Útil como ejemplo de la pantalla **más simple posible** en Compose (sin estado, sin red).

### Cómo se conecta con las demás partes
Depende de `PropertyRepository`, `TourRepository`, `UserRepository`, `AuthRepository` y `CloudinaryManager` (Persona 1). El modelo `TourBooking.status` que manipulas aquí es el mismo que la Persona 3 nunca toca pero que sí se originó en `ScheduleTourViewModel` (que también es tuyo). El cierre de sesión te devuelve a la pantalla de Login (Persona 2).

### Preguntas típicas
- ¿Cómo evita la app que alguien agende una visita a su propia propiedad? → comparación `ownerId == currentUserId` antes de guardar.
- ¿Cómo se relacionan "Mis Tours" y "Solicitudes Recibidas" si son pantallas distintas? → son la misma colección de Firestore (`tours`) vista desde dos ángulos: `userId` (visitante) vs `propertyOwnerId` (dueño).
- ¿Por qué `saveProperty` sube la imagen antes de escribir en Firestore? → porque necesita la URL final de Cloudinary para guardarla como `imageUrl` de la propiedad; si escribiera antes, el documento quedaría sin foto.

---

## 5. Cómo se conectan las 4 partes (para preguntas cruzadas)

Un recorrido típico de usuario que toca las 4 partes:

1. **Persona 2**: `NavHost` decide que no hay sesión → muestra `LoginScreen`. El usuario inicia sesión → `AuthViewModel` llama a `AuthRepository` (**Persona 1**).
2. Con sesión iniciada, `NavHost` navega a `Home` (**Persona 3**). `HomeViewModel` pide propiedades a `PropertyRepository` y siembra datos si hace falta (**Persona 1**), usando los colores/tipografía y el fondo `NatureBackdrop` (**Persona 2**).
3. El usuario abre el detalle de una propiedad, la marca como favorita (`SavedRepository`, **Persona 1**) y agenda un tour (**Persona 4**, `ScheduleTourViewModel` → `TourRepository`).
4. El dueño de esa propiedad entra a "Mis publicaciones" (**Persona 4**) y ve la solicitud en "Recibidas"; la acepta, lo que cambia el `status` en el mismo documento de Firestore que el visitante está viendo en tiempo real en "Mis Tours".
5. En cualquier momento, el usuario va a su Perfil (**Persona 4**), cambia su foto (Cloudinary, **Persona 1**) o cierra sesión, lo que lo regresa a Login (**Persona 2**).

Si el profesor pregunta "¿qué pasa si...", casi siempre la respuesta involucra: *qué ViewModel actúa → qué Repository llama → qué colección/documento de Firestore toca → qué otra pantalla también está mirando esos mismos datos*.

## 6. Glosario rápido para toda la exposición

- **Composable**: función marcada `@Composable` que describe una parte de la UI declarativamente (qué se ve, no cómo dibujarlo paso a paso).
- **ViewModel**: clase que sobrevive a cambios de configuración (como rotar la pantalla) y contiene el estado + la lógica de una pantalla, separada de la UI.
- **StateFlow / collectAsState()**: forma de exponer un valor observable desde el ViewModel; la UI se "suscribe" y se redibuja sola cuando cambia.
- **Flow / callbackFlow**: un `Flow` normal emite valores bajo demanda; `callbackFlow` envuelve una API basada en callbacks (como `addSnapshotListener` de Firestore) para convertirla en un `Flow` que puede recolectarse con `collect { }`.
- **suspend fun**: función que puede "pausarse" sin bloquear el hilo mientras espera una operación larga (red, disco); solo se puede llamar desde una coroutine.
- **Hilt (`@Inject`, `@HiltViewModel`, `@Module`, `@Singleton`)**: sistema de inyección de dependencias — en vez de que cada clase construya sus propias dependencias, las pide en el constructor y Hilt se las entrega ya armadas.
- **Result&lt;T&gt;**: tipo de Kotlin que representa éxito (`Result.success(valor)`) o fracaso (`Result.failure(excepción)`) sin usar excepciones para controlar el flujo.
- **sealed class**: una clase cuyas subclases son un conjunto cerrado y conocido (por ejemplo, `AuthUiState` solo puede ser `Idle`, `Loading`, `Success` o `Error`).
