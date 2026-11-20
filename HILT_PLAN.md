# Plan: Dependency Injection met Hilt

Status: **uitgevoerd** (stappen 1 t/m 8). De optionele instrumented Hilt-test is bewust weggelaten.

## 1. Doel

De app gebruikt nu *manuele* DI (`AppContainer` + `DefaultAppContainer` + `ToDoApplication.container` + een `Factory` in elke ViewModel). We vervangen dat door **Hilt**, de DI-library die Android Developers aanraadt. Daarbij ruimen we overbodige code op, houden we de structuur netjes en schrijven we unit tests die slagen.

## 2. Huidige situatie (wat er nu staat)

| Onderdeel | Nu | Probleem |
|---|---|---|
| `ToDoApplication` | `lateinit var container: AppContainer`, maakt `DefaultAppContainer(this)` | Handmatig, wordt door Hilt overbodig |
| `data/AppContainer.kt` | Bouwt Retrofit, OkHttp, Json, repositories | Wordt vervangen door Hilt-modules |
| `data/ToDoDatabase.kt` | Statische singleton `getDatabase(context)` + callback die zichzelf opnieuw opvraagt en `CoroutineScope(Dispatchers.IO)` aanmaakt | Context-plumbing, niet testbaar |
| `ToDoListViewModel`, `ToDoDetailViewModel`, `ToDoEditViewModel` | Elk een `companion object` met `viewModelFactory { ... (this[APPLICATION_KEY] as ToDoApplication).container ... }` | Boilerplate, cast naar `ToDoApplication` |
| `ToDoDetailView` / `ToDoEditView` | Hebben `LaunchedEffect` die `loadToDo(...)` / `loadToDoToEdit(...)` aanroept, plus dubbele parameters (`todoNumber` én `toDo`, `todoNumberToEdit` én `toDoToEdit`) | Laden hoort in de ViewModel, parameters zijn overbodig |
| `ToDoNav` | `NavEntry` + `when(key)` met `else -> error(...)`, `viewModel(factory = ...)`, geen `entryDecorators` | Zonder `rememberViewModelStoreNavEntryDecorator` leeft de ViewModel in de Activity-store: twee detailschermen met een verschillend `number` kunnen dezelfde ViewModel hergebruiken |
| Tests | `ToDoTest`, `UserTest`, `DatasourceTest`, `ExampleUnitTest` (enkel models/datasource) | Geen enkele ViewModel of repository getest |

## 3. Voorgestelde nieuwe structuur

```
be.aidenstorme.todoapplication
├── ToDoApplication.kt          @HiltAndroidApp, verder leeg
├── MainActivity.kt             @AndroidEntryPoint
├── di/                         (NIEUW)
│   ├── DatabaseModule.kt       Room database + DAOs
│   ├── NetworkModule.kt        Json, OkHttpClient, Retrofit, BoredApiService (base URL op één plaats)
│   ├── RepositoryModule.kt     @Binds: interface -> implementatie
│   └── CoroutineModule.kt      @ApplicationScope CoroutineScope (voor het seeden van de database)
├── data/                       repositories krijgen @Inject constructor; AppContainer.kt verdwijnt
├── ui/viewmodels/             @HiltViewModel; Detail + Edit met assisted injection
└── ui/navigation/ToDoNav.kt    entryProvider-DSL + hiltViewModel(creationCallback)
```

### Wat wordt `@Provides`, wat `@Binds`?
- `@Binds` (abstracte module): `ToDoRepository <- OfflineToDoRepository`, `UserRepository <- OfflineUserRepository`, `ActivityRepository <- NetworkActivityRepository`. De implementaties krijgen `@Inject constructor(...)`.
- `@Provides`: alles waar we de klasse niet zelf kunnen annoteren (Room, Retrofit, OkHttp, Json, DAOs, `BoredApiService`).
- Scope: `@Singleton` op database, Retrofit-stack en repositories (`SingletonComponent`).

## 4. Dependencies (nieuw: **hier heb ik jouw goedkeuring voor nodig**)

AGENTS.md zegt: geen nieuwe libraries zonder eerst te vragen. Hilt staat niet in de uitzonderingslijst. Voorstel (versies opgezocht):

| Library | Versie | Waarvoor |
|---|---|---|
| `com.google.dagger.hilt.android` (Gradle plugin) | 2.60.1 | Hilt-codegeneratie (2.59+ is nodig voor AGP 9) |
| `com.google.dagger:hilt-android` | 2.60.1 | Runtime |
| `com.google.dagger:hilt-compiler` (via `ksp`) | 2.60.1 | Annotation processor; KSP gebruiken we al voor Room |
| `androidx.hilt:hilt-lifecycle-viewmodel-compose` | 1.4.0 | `hiltViewModel(creationCallback = ...)`, de aanpak uit de Navigation 3-recepten |
| `org.jetbrains.kotlinx:kotlinx-coroutines-test` | 1.11.0 | `runTest` / test dispatcher voor ViewModel-tests (alleen `testImplementation`) |

Bewust **niet** toegevoegd: `hilt-navigation-compose` (Navigation 2), Turbine (we komen toe met `runTest` + `StateFlow.value`), MockK/Mockito (we gebruiken eenvoudige fake-klassen, leesbaarder om uit te leggen).

Alles via `gradle/libs.versions.toml` + `app/build.gradle.kts` (+ het root-`build.gradle.kts` voor `alias(libs.plugins.hilt) apply false`).

## 5. Stappen

> Na elke stap: project laten compileren (`./gradlew assembleDebug`). Stap 1 eerst afsluiten, omdat Hilt + AGP 9 het grootste risico is.

### Stap 1: Hilt toevoegen (build-configuratie)
- `libs.versions.toml`: versies `hilt`, `androidxHilt`, `coroutinesTest`; libraries en plugin `hilt = { id = "com.google.dagger.hilt.android" }`.
- Root-`build.gradle.kts`: plugin `apply false`. `app/build.gradle.kts`: plugin + `implementation` + `ksp(libs.hilt.compiler)` + `testImplementation(coroutines-test)`.
- Gradle sync + `assembleDebug` met nog niets gewijzigd. Zo weten we of de plugin-combinatie (AGP 9.4.1, Kotlin 2.2.10, KSP 2.3.10) werkt.

### Stap 2: Application en Activity
- `ToDoApplication`: `@HiltAndroidApp`, `container` en `onCreate` weg.
- `MainActivity`: `@AndroidEntryPoint`.
- Manifest blijft hetzelfde (`android:name=".ToDoApplication"` staat er al).

### Stap 3: Hilt-modules in `di/`
- `DatabaseModule`: `@Provides @Singleton ToDoDatabase` (met `@ApplicationContext`), `@Provides userDao()` / `toDoDao()`.
  - De callback die `Datasource` seedt wordt een eigen klasse met `@Inject constructor(Provider<ToDoDatabase>, @ApplicationScope CoroutineScope)`. De statische `Instance`/`getDatabase(context)` en de `CoroutineScope(Dispatchers.IO)` in `onCreate` verdwijnen.
- `NetworkModule`: `Json`, `OkHttpClient`, `Retrofit` (base URL als `const val` op één plaats), `BoredApiService`.
- `RepositoryModule`: drie `@Binds`.
- `CoroutineModule`: `@ApplicationScope` qualifier + `CoroutineScope(SupervisorJob() + Dispatchers.IO)`.
- `OfflineToDoRepository`, `OfflineUserRepository`, `NetworkActivityRepository`: `@Inject constructor`.
- `data/AppContainer.kt` verwijderen.

### Stap 4: ViewModels
- `ToDoListViewModel`: `@HiltViewModel` + `@Inject constructor(toDoRepository, userRepository)`. `companion object Factory` weg.
- `ToDoDetailViewModel`: `@HiltViewModel(assistedFactory = Factory::class)`, `@AssistedInject constructor(@Assisted todoNumber: Int, repos...)`, geneste `@AssistedFactory interface Factory { fun create(todoNumber: Int): ToDoDetailViewModel }`. Laden gebeurt in `init`, `loadToDo(...)` wordt `private` of verdwijnt.
- `ToDoEditViewModel`: idem, met `@Assisted todoNumber: Int?` (`null` = nieuwe to-do) en `ActivityRepository` erbij. Het `loadToDoToEdit(...)` verdwijnt uit de view.
- Constructors blijven `Context`-vrij en verwijzen niet naar een view of navigator (conform AGENTS.md).

### Stap 5: Views en navigatie
- `ToDoListView`: standaardwaarde `viewModel: ToDoListViewModel = hiltViewModel()`.
- `ToDoDetailView` / `ToDoEditView`: `LaunchedEffect` en de overbodige parameters (`todoNumber`, `toDo`, `todoNumberToEdit`, `toDoToEdit`) weghalen; de ViewModel wordt (zoals nu) door de navigatie meegegeven. Parameterlijst blijft in AGENTS.md-volgorde (`modifier` eerst met standaardwaarde zoals de rest van de code, callbacks laatst).
- `ToDoNav`:
  - `entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator())` (verplicht zodat elke key z'n eigen ViewModel krijgt; uit de Navigation 3 Hilt-recipe).
  - `entryProvider { entry<Screen.ToDoList> { ... } entry<Screen.ToDoDetail> { key -> ... } ... }` in plaats van `NavEntry` + `when` + `else -> error`.
  - `hiltViewModel<ToDoDetailViewModel, ToDoDetailViewModel.Factory>(creationCallback = { it.create(key.todoNumber) })`.
  - `androidx.compose.ui.Modifier` volledig gekwalificeerd -> gewone import.
- `Navigator` blijft zoals nu (buiten scope; enkel id's als primitieven door de navigatie).

### Stap 6: Opruimen (overbodige code)
Na stap 5 controleren en verwijderen wat niet meer gebruikt wordt:
- `AppContainer.kt`, `DefaultAppContainer`, `ToDoApplication.container`
- alle `companion object`-factories in de ViewModels en bijhorende imports (`ViewModelProvider`, `initializer`, `viewModelFactory`, `ToDoApplication`)
- `ToDoDatabase.getDatabase(...)`/`Instance`
- ongebruikte imports; controleren of `HomeView.kt` en `ToDoApp.kt` nog gebruikt worden (alleen verwijderen als ze echt dood zijn)

### Stap 7: Tests (unit tests, `app/src/test`)
Zonder emulator uitvoerbaar met `./gradlew testDebugUnitTest`. Bestaande tests blijven onaangeroerd.

| Testbestand | Test |
|---|---|
| `testutil/MainDispatcherRule.kt` | JUnit-rule die `Dispatchers.Main` vervangt door een test dispatcher (nodig voor `viewModelScope`) |
| `testutil/FakeToDoRepository.kt`, `FakeUserRepository.kt`, `FakeActivityRepository.kt` | Eenvoudige fakes met `MutableStateFlow`; fake activity repo kan succes of exception teruggeven |
| `OfflineToDoRepositoryTest` / `OfflineUserRepositoryTest` | Repository delegeert naar de DAO (fake DAO) |
| `ToDoListViewModelTest` | UiState bevat to-do's + users uit de repositories; update na wijziging in de repo |
| `ToDoDetailViewModelTest` | Laadt de juiste to-do + toegewezen/aanmaker-user; `onAcceptanceDoneChange(true)` zet `finishedOnDate` enkel als er een gebruiker toegewezen is; vinkjes updaten de repo |
| `ToDoEditViewModelTest` | `null` -> leeg formulier; bestaand nummer -> velden voorgevuld; `onSave` voegt toe (nieuw) of past aan (bestaand); `fetchRandomActivity` zet titel/beschrijving bij succes, `ActivityError.NETWORK` bij `IOException`, `ActivityError.SERVER` bij `HttpException` |

De Hilt-graph zelf wordt al bij het bouwen gevalideerd (ontbrekende bindings = compilefout).

**Optioneel (vraag onderaan):** één instrumented test met `@HiltAndroidTest` die controleert dat de graph op een echt toestel start. Dit vraagt `hilt-android-testing`, een eigen `HiltTestRunner` en een emulator.

### Stap 8: AGENTS.md bijwerken
Zodat toekomstige agents niet terugvallen op manuele DI:
- Tech stack: Hilt-libraries toevoegen aan de uitzonderingslijst.
- Architectuur: map `di` toevoegen.
- "Repository & dependency injection": `AppContainer`/`DefaultAppContainer`/`viewModelFactory`-regels vervangen door Hilt (`@HiltAndroidApp`, `@Module`/`@Binds`/`@Provides`, `@HiltViewModel`, `@AssistedInject` + `hiltViewModel(...)` in de entry provider).
- Verouderde regel "Nog geen database" in *Over het project* aanpassen.
- Opmerking: AGENTS.md heeft nu al niet-gecommitte wijzigingen van jou; ik pas enkel de bovenstaande secties aan.

## 6. Definition of Done

- [ ] `./gradlew assembleDebug` compileert zonder fouten
- [ ] `./gradlew testDebugUnitTest` slaagt (nieuwe én bestaande tests; geen tests verwijderd of uitgeschakeld)
- [ ] Geen `AppContainer`, `getDatabase(...)` of `viewModelFactory`-code meer
- [ ] Alle regels uit AGENTS.md gevolgd (`modifier`-parameter, geen hardcoded tekst, geen `@Preview`, ...)
- [ ] App start in de emulator en lijst, detail, toevoegen/wijzigen en "I'm lazy"-knop werken nog
- [ ] Korte samenvatting van gewijzigde bestanden

## 7. Risico's

- **AGP 9 + Hilt-plugin**: vereist Hilt 2.59 of hoger; dat is waarom stap 1 apart wordt afgesloten.
- **Database seeden**: de huidige callback roept `getDatabase` recursief aan. Met `Provider<ToDoDatabase>` en een `@ApplicationScope` blijft het gedrag gelijk. Bestaande installaties met een al aangemaakte database zijn niet aangetast (seed gebeurt enkel in `onCreate`).
- **ViewModel-scope per scherm**: door de `entryDecorators` krijgt elk scherm zijn eigen ViewModel. Dat is het gewenste gedrag, maar verandert het huidige (foutieve) hergebruik per Activity.

## 8. Vragen vóór ik begin

1. Mag ik de Hilt-dependencies uit hoofdstuk 4 toevoegen (inclusief `kotlinx-coroutines-test`)?
2. Wil je de optionele instrumented Hilt-test (emulator nodig), of volstaan de unit tests?
3. Is de nieuwe map `di/` voor de modules oké? (Alternatief: in `data/`, maar AGENTS.md reserveert die voor repositories en de oude `AppContainer`.)
4. Mag ik de `entryProvider { entry<...> }`-DSL gebruiken in `ToDoNav`, zoals in de Navigation 3 Hilt-recepten?

Antwoord met "proceed" (en eventuele aanpassingen) en ik voer de stappen 1 t/m 8 uit.
