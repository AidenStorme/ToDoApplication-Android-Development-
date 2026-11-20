# AGENTS.md - Instructies & Richtlijnen voor AI Coding Agents

Elke regel staat maar op één plek. Waar een regel ergens anders hoort, wordt ernaar verwezen.

## Over het project
- To-do app voor de cursus AI Driven Android Development (VIVES). Het doel is leren: schrijf eenvoudige, leesbare code die ik kan uitleggen.
- Drie schermen: **List** (alle to-do's), **Detail** (één to-do op basis van zijn `number`) en **Edit** (to-do toevoegen of wijzigen). Navigatie via Navigation 3.
- Data: to-do's en users worden lokaal bewaard met Room. De knop "I'm lazy" haalt een activiteit op via een externe API (Retrofit).

## Tech stack
- Kotlin, Jetpack Compose, Material 3
- minSdk: 36 / targetSdk: 37 / compileSdk: 37
- Libraries: enkel wat in `gradle/libs.versions.toml` staat. Voeg geen nieuwe libraries toe zonder het eerst te vragen, behalve:
  - `material-icons-core` / `material-icons-extended` (zie Invoervelden)
  - `lifecycle-viewmodel-compose` voor ViewModels en `lifecycle-runtime-compose` voor `collectAsStateWithLifecycle()`
  - Retrofit + de kotlinx.serialization converter, `kotlinx-serialization-json` en de Kotlin serialization plugin voor netwerkcalls
  - Coil (`coil-compose`) voor afbeeldingen van het internet
  - Room (`room-runtime`, `room-ktx`, `room-compiler` via KSP) voor de lokale database
  - Navigation 3 (`androidx.navigation3:navigation3-runtime`, `androidx.navigation3:navigation3-ui`) en `androidx.lifecycle:lifecycle-viewmodel-navigation3` (een ViewModel per scherm)
  - Hilt voor dependency injection (zie Dependency injection): de Hilt Gradle plugin, `hilt-android`, `hilt-compiler` (via KSP) en `androidx.hilt:hilt-lifecycle-viewmodel-compose` voor `hiltViewModel()`
  - `kotlinx-coroutines-test` voor unit tests (alleen `testImplementation`)

## Architectuur
- Lagen: View → ViewModel → Repository → DAO (Room) of ApiService (Retrofit). Een laag verwijst enkel naar de laag eronder, nooit omgekeerd.
- Separation of concerns: de view toont enkel state en stuurt events door. Geen business logic in de views.
- De repository is de enige toegang tot data voor de ViewModels. Een view of ViewModel gebruikt nooit rechtstreeks een DAO of ApiService.
- Modellen in `models` zijn pure Kotlin: geen verwijzingen naar `R`, `Context` of andere Android-klassen. De vertaling naar tekst (bv. status → `R.string.status_new`) gebeurt in de UI-laag.
- Hoe objecten aangemaakt en doorgegeven worden: zie Dependency injection.

## Mappenstructuur & naamgeving
Basispackage: `be.aidenstorme.todoapplication`. Packagenamen zijn volledig lowercase, zonder hoofdletters of underscores.

| Wat | Package | Naam | Voorbeeld |
|---|---|---|---|
| Scherm (volledige view) | `ui/views` | eindigt op `View` | `ToDoListView` |
| Herbruikbare component | `ui` | prefix `App`, eindigt NIET op `View` | `AppTextField`, `AppToDoCard` |
| ViewModel | `ui/viewmodels` | `<Scherm>ViewModel` | `ToDoListViewModel` |
| UiState | `ui/viewmodels` | `<Scherm>UiState`, eigen bestand | `ToDoListUiState` |
| Navigatie | `ui/navigation` | composable eindigt op `Nav` | `ToDoNav`, `Navigator`, `Screen` |
| Data class / Room-entity | `models` | naam van het ding | `ToDo`, `User` |
| API-interface | `network` | eindigt op `ApiService` | `BoredApiService` |
| Repository | `data` | interface `<Data>Repository`, implementatie `Offline<Data>Repository` (Room) of `Network<Data>Repository` (API) | `ToDoRepository` / `OfflineToDoRepository` |
| DAO & database | `data` | `<Data>Dao`, `<App>Database` | `ToDoDao`, `ToDoDatabase` |
| Hilt-module | `di` | eindigt op `Module` | `DatabaseModule`, `NetworkModule`, `RepositoryModule` |
| Hulpfuncties | `utility` | `object Utility` | `Utility.checkIntValue(...)` |

- `<Scherm>` is de naam van de view zonder `View` (`ToDoEditView` → `ToDoEditViewModel` + `ToDoEditUiState`).
- De naam van de functie of klasse is gelijk aan de bestandsnaam (bv. `ToDoListView.kt` bevat `fun ToDoListView(...)`).
- Namen van resources (bv. afbeeldingen/iconen in `drawable` of `mipmap`) zijn volledig lowercase.

## ViewModels & state
- Elk scherm heeft één eigen ViewModel en één eigen UiState. Deel nooit een ViewModel tussen schermen, ook niet als een nieuw scherm op een bestaand lijkt. In deze app:
  - **List**: `ToDoListViewModel` + `ToDoListUiState` voor `ToDoListView`.
  - **Detail**: `ToDoDetailViewModel` + `ToDoDetailUiState` voor `ToDoDetailView`, krijgt het `number` van de to-do.
  - **Edit** (toevoegen én wijzigen): `ToDoEditViewModel` + `ToDoEditUiState` voor `ToDoEditView`, krijgt een optioneel `number` (`null` = nieuwe to-do).
- De ViewModel is de enige eigenaar van de state van een scherm (single source of truth). Hij verwijst nooit naar een view, een composable, een navigator of een `Context`.
- Een UiState is een `data class` met enkel `val`s en standaardwaarden.
- Naar buiten geeft de ViewModel enkel een read-only `StateFlow` als `val uiState`.
  - State die uit Room komt: zet de `Flow` van de repository om met `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), XxxUiState())`.
  - State die de gebruiker invult (bv. formulier op het Edit-scherm): een private `MutableStateFlow`, aangepast met `_uiState.update { it.copy(...) }`.
- Data wordt geladen in het `init`-blok van de ViewModel, niet via `LaunchedEffect` in de view.
- Events (bv. op save klikken) roepen een functie van de ViewModel aan. De view past de state nooit zelf aan.
- Laadt een scherm zijn inhoud van het internet, dan is de UiState een `sealed interface` met `Loading`, `Success(data)` en `Error`. De view toont voor elk van de drie een ander scherm (laadindicator, inhoud, foutmelding).
- Is de netwerkcall een actie binnen een scherm dat al inhoud heeft (bv. "I'm lazy" op het Edit-scherm), dan komt een eventuele fout als veld in de bestaande UiState en toont de view een foutmelding.
- In de view: lees de state met `val uiState by viewModel.uiState.collectAsStateWithLifecycle()`.
- `rememberSaveable` enkel voor state die puur bij de UI van één component hoort (bv. open/dichtgeklapt). Gebruik `by`, niet `=`: `var expanded by rememberSaveable { mutableStateOf(false) }`.

## Composables
- Elke composable functie heeft de annotation `@Composable`.
- Elke composable functie heeft ALTIJD de parameter `modifier: Modifier = Modifier`, ook als die niet gebruikt wordt bij het aanroepen. Vergeet dit nooit.
- Geef die `modifier`-parameter altijd door aan het buitenste element van de composable (bv. `Column(modifier = modifier)`).
- Herbruikbare componenten (in `ui`) zijn altijd stateless (state hoisting): waarden en callbacks (bv. `value` en `onValueChange`) komen binnen als parameters, nooit hardcoded of met interne state.
- Schermen (in `ui/views`) krijgen hun ViewModel als parameter. Geef een ViewModel nooit door aan een kleinere composable: geef enkel waarden en callbacks door.
- Volgorde van parameters: eerst verplichte waarden, dan optionele parameters met een standaardwaarde (incl. `modifier: Modifier = Modifier`), en de callback (bv. `onValueChange`) als laatste, zodat de composable met trailing lambda aangeroepen kan worden: `AppTextField(value = name, label = stringResource(R.string.name)) { name = it }`.
- Gebruik voor tekst altijd een composable (`Text`).
- Gebruik GEEN `@Preview`-functies en geen Preview-imports. De app wordt getest in de emulator.
- Kleuren komen altijd uit het thema (`MaterialTheme.colorScheme.primary`, `.error`, `.outline`, …), nooit hardcoded (`Color.Red`, `Color.LightGray`, …).

## Layout & lijsten
- `MainActivity` roept `enableEdgeToEdge()` aan.
- Elk scherm gebruikt een `Scaffold` en geeft de `innerPadding` door als padding aan de content, zodat niets achter de status- of navigatiebalk terechtkomt.
- Gebruik `Column` om elementen onder elkaar te plaatsen en `Row` om ze naast elkaar te plaatsen. Verdeel ruimte met gewichten (`Modifier.weight(...)`) via de modifier.
- Lijsten toon je altijd met `LazyColumn`, nooit met een gewone `Column`.
- Itereer met `items(list, key = { it.number })`: geef altijd een unieke, stabiele `key` mee (de primary key van het item).
- Elk item in de lijst wordt getoond in een `ElevatedCard` met een `modifier`.

## Room (lokale database)
- Entities zijn data classes in `models` met `@Entity(tableName = "...")` en een `@PrimaryKey(autoGenerate = true)`.
- Een relatie naar een andere entity bewaar je als id (bv. `assignedToUserId: Int?`), nooit als object (`assignedUser: User`). Leg er een `ForeignKey` op met een bewust gekozen `onDelete`, en zet altijd een `Index` op die kolom.
- Datums worden bewaard als `Long` (epoch millis, `System.currentTimeMillis()`). De omzetting naar leesbare tekst gebeurt in de UI-laag.
- DAO's (in `data`): leesfuncties geven een `Flow` terug (`fun getAllToDos(): Flow<List<ToDo>>`), schrijffuncties zijn `suspend` (`@Insert`, `@Update`, `@Delete`).
- Er is één database-klasse (`ToDoDatabase`) met één instantie (singleton, aangemaakt in `DatabaseModule`). Voorbeelddata wordt één keer ingeladen in de `onCreate`-callback van de database (`ToDoDatabaseCallback`).
- Verandert een entity (kolom erbij, type gewijzigd, …), verhoog dan `version` in `@Database`. Tijdens de ontwikkeling mag `fallbackToDestructiveMigration(dropAllTables = true)`; vraag het eerst als er een echte migration nodig is.

## Netwerk & API (Retrofit)
- De `INTERNET`-permission staat in `AndroidManifest.xml`.
- Elke API heeft een interface in `network` (bv. `BoredApiService`) met `suspend`-functies en Retrofit-annotaties (`@GET`, `@POST`, …). De interface bevat geen Retrofit-setup: die gebeurt bij Dependency injection.
- Gebruik `kotlinx.serialization` om JSON om te zetten: data classes in `models` krijgen `@Serializable`, en `@SerialName("...")` als de JSON-naam verschilt van de Kotlin-naam.
- De base URL staat op één plaats (bij Dependency injection), niet verspreid in de code.

## Coroutines
- Netwerkcalls, databasewerk en andere trage taken zijn `suspend`-functies (of een `Flow`).
- Een ViewModel start ze met `viewModelScope.launch { ... }`, nooit `GlobalScope` of `runBlocking`. Room en Retrofit zijn zelf main-safe: `withContext(Dispatchers.IO)` is daarvoor niet nodig.
- Vang netwerkfouten op met `try/catch` (`IOException` voor netwerkproblemen, `HttpException` voor foutcodes van de server) en zet het resultaat in de UiState (zie ViewModels & state). Gebruik nooit enkel `printStackTrace()`.

## Navigatie (Navigation 3)
- Gebruik bij het implementeren van navigatie altijd de Navigation 3-skill en volg de aanpak daaruit.
- `MainActivity` roept niet rechtstreeks een view aan, maar de composable `ToDoNav` (in `ui/navigation`). Daarin staat `NavDisplay` met de entry provider.
- Destinations staan in `sealed interface Screen : NavKey` en zijn allemaal `@Serializable`:
  - een scherm zonder argumenten is een `data object` (bv. `Screen.ToDoList`);
  - een scherm met argumenten is een `data class` (bv. `Screen.ToDoDetail(val todoNumber: Int)`).
- Geef tussen schermen enkel primitieve types of serializable waarden door (bv. een `number`), nooit objecten of Room-entities. Het doelscherm haalt de rest zelf op met die waarde.
- De backstack wordt aangemaakt met `rememberNavBackStack(Screen.ToDoList)`, zodat hij een configuratiewijziging overleeft. Een `Navigator`-klasse met `navigate(...)` en `goBack()` beheert die backstack.
- Extra navigatieregels horen in de `Navigator`, niet in een view of ViewModel: bv. geen dubbele entries (eerst pop als de route al in de stack zit).
- `NavDisplay` krijgt `entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator())`, zodat elke entry zijn eigen ViewModel krijgt (bv. een nieuwe `ToDoDetailViewModel` per to-do).
- De entry provider bepaalt per key welke view getoond wordt; het is de enige plek die de koppeling tussen de schermen kent.
- Views navigeren nooit zelf en de ViewModel kent geen navigator: navigatie verloopt via callbacks (bv. `onToDoClick`, `onBack`), doorgegeven door de entry provider in `ToDoNav`.

## Dependency injection
Deze regels gelden altijd, los van hoe DI technisch gebeurt:
- Een klasse krijgt alles wat ze nodig heeft via de constructor. Een ViewModel maakt nooit zelf een repository, DAO of ApiService aan.
- Er is één plek waar alle objecten (database, Retrofit, ApiServices, repositories) aangemaakt en aan elkaar gekoppeld worden.
- ViewModels zijn `Context`-vrij.

Invulling: **Hilt**.
- `ToDoApplication` heeft `@HiltAndroidApp` (en is geregistreerd in `AndroidManifest.xml` met `android:name`), `MainActivity` heeft `@AndroidEntryPoint`.
- Alle objecten worden aangemaakt in Hilt-modules in de package `di`, allemaal met `@InstallIn(SingletonComponent::class)`:
  - `DatabaseModule`: de Room-database (met de seed-callback) en de DAO's;
  - `NetworkModule`: `Json`, OkHttp, Retrofit (incl. de base URL) en de ApiServices;
  - `RepositoryModule`: per repository een `@Binds` van de interface naar zijn implementatie;
  - `CoroutineModule`: de `@ApplicationScope` `CoroutineScope`.
- Gebruik `@Binds` voor interface → implementatie en `@Provides` voor klassen die we niet zelf kunnen annoteren (Room, Retrofit, OkHttp, …). Repository-implementaties (`Offline…`, `Network…`) hebben een `@Inject constructor(...)`.
- ViewModels hebben `@HiltViewModel`:
  - Zonder navigatieargumenten: `@Inject constructor(...)`. De view krijgt hem in zijn parameterlijst: `viewModel: ToDoListViewModel = hiltViewModel()`.
  - Met navigatieargumenten (bv. een `number`): `@HiltViewModel(assistedFactory = Factory::class)`, `@AssistedInject constructor(@Assisted todoNumber: Int, ...)` en een geneste `@AssistedFactory interface Factory`. De entry provider in `ToDoNav` maakt de ViewModel aan met `hiltViewModel<ToDoDetailViewModel, ToDoDetailViewModel.Factory>(creationCallback = { it.create(key.todoNumber) })` en geeft hem door aan de view.
- In unit tests maak je een ViewModel gewoon met zijn constructor en fake repositories: daar is Hilt niet voor nodig.

## Invoervelden (TextField)
- Geef een `TextField` altijd een `value` (uit de UiState) en een `onValueChange` die een functie van de ViewModel aanroept (bv. `onValueChange = { viewModel.onTitleChange(it) }`).
- Geef een label mee via `label = { Text(stringResource(R.string.…)) }`.
- Gebruik voor iconen (bv. `leadingIcon`) de Material Icons van Google. Voeg daarvoor `androidx.compose.material:material-icons-core` toe via de version catalog (`gradle/libs.versions.toml` + `app/build.gradle.kts`). Heb je een icoon nodig dat niet in de basisset zit, voeg dan `material-icons-extended` toe op dezelfde manier. Gebruik geen iconen die niet beschikbaar zijn in de toegevoegde dependencies.
  - Opmerking: Google raadt deze library niet meer aan voor nieuwe apps (Material Symbols als XML-drawable is de opvolger). Voor deze cursus blijven we ze gebruiken.
- Importeer elk icoon expliciet (bv. `import androidx.compose.material.icons.filled.Person`). Geen wildcard-imports (`.*`), volgens de Android Kotlin style guide.
- Decoratieve iconen (bv. een `leadingIcon`) krijgen `contentDescription = null`.
- Velden die enkel cijfers mogen bevatten krijgen `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)`.
- Valideer numerieke invoer met de utility-functie, zodat de laatste geldige waarde behouden blijft: `onValueChange = { viewModel.onScoreChange(Utility.checkIntValue(it) ?: uiState.score) }`.

## Utility-functies
- Hulpfuncties staan in de package `utility`, in een `object Utility` (geen `class`), zodat ze statisch aangeroepen worden: `Utility.checkIntValue(...)`.
- `checkIntValue(value: String): String?` werkt zo: lege string → `""` (zodat de gebruiker het veld kan leegmaken), geldige int via `value.toIntOrNull()` → `result.toString()`, anders → `null`.

## Afbeeldingen
- Afbeeldingen van een URL tonen met Coil `AsyncImage`, altijd met een `placeholder` (laden) en een `error`-afbeelding uit `res/drawable`.
- Een `contentDescription` komt uit `strings.xml`, behalve bij decoratieve afbeeldingen (`null`).

## Resources
- Zet geen hardcoded teksten in de code. Alle teksten komen in `res/values/strings.xml` en worden opgehaald met `stringResource(R.string.…)`.

## Manifest
- Nodige permissions (internet, camera, …) worden toegevoegd in `AndroidManifest.xml`.
- Nieuwe activities worden geregistreerd in `AndroidManifest.xml`.

## Dependencies
- Externe libraries worden toegevoegd in `app/build.gradle.kts` via de version catalog.
- Versies worden beheerd in `gradle/libs.versions.toml`, nooit rechtstreeks in `build.gradle.kts`.

## Werkwijze
- Maak bij grotere wijzigingen eerst een plan en wijzig geen bestanden tot ik bevestig met "proceed".
- Doe enkel wat gevraagd wordt. Voeg geen nieuwe lagen, schermen of libraries toe zonder het eerst te vragen.
- Controleer na elke wijziging of het project compileert en los fouten op voor je zegt dat je klaar bent.

## Definition of Done
Een taak is pas klaar als:
- het project compileert (`./gradlew assembleDebug`) zonder fouten
- de unit tests slagen (`./gradlew testDebugUnitTest`)
- nieuwe logica in een ViewModel een unit test heeft, met een fake repository (een eigen klasse die de repository-interface implementeert) in plaats van de echte database of API
- er geen bestaande tests verwijderd of uitgeschakeld zijn om ze te laten slagen
- alle regels in dit bestand gevolgd zijn
- alle teksten in `res/values/strings.xml` staan
- je een korte samenvatting gegeven hebt van welke bestanden je gewijzigd hebt
