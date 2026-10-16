# AGENTS.md - Instructies & Richtlijnen voor AI Coding Agents

## Over het project
- To-do app voor de cursus AI Driven Android Development (VIVES). Het doel is leren: schrijf eenvoudige, leesbare code die ik kan uitleggen.
- Huidige stand: lijst van to-do items als startscherm, nog geen navigatie, nog geen database.

## Tech stack
- Kotlin, Jetpack Compose, Material 3
- minSdk: 36 / targetSdk: 37 / compileSdk: 37
- Libraries: enkel wat in `gradle/libs.versions.toml` staat. Voeg geen nieuwe libraries toe zonder het eerst te vragen, behalve `androidx.compose.material:material-icons-core` en `material-icons-extended` indien nodig (zie Invoervelden) en `lifecycle-viewmodel-compose` voor ViewModels.

## Architectuur
- Views in `ui/views`, herbruikbare componenten in `ui`, ViewModels in `ui/viewmodels`, data classes in `models`, hulpfuncties in `utility`.
- Separation of concerns: de view toont enkel state en stuurt events door. Geen business logic in de views.
- Elke view heeft één eigen ViewModel met dezelfde naam + `ViewModel` (bv. `AddUserView` → `AddUserViewModel`).
- De view maakt de ViewModel aan via `val viewModel: AddUserViewModel = viewModel()`. De ViewModel verwijst nooit naar een view, een composable of een `Context`.

## Mappenstructuur & naamgeving
- Plaats alle views (volledig scherm) in de map `ui/views`.
- De naam van elke view (volledig scherm) eindigt op `View` (bv. `ToDoListView`, `AddEditTodoView`).
- Herbruikbare componenten (kleine bouwstenen die in views gebruikt worden) staan in `ui`, eindigen NIET op `View` en krijgen de prefix `App` (bv. `AppTextField`, `AppButton`).
- De naam van de functie is gelijk aan de bestandsnaam (bv. `ToDoListView.kt` bevat `fun ToDoListView(...)`).
- Namen van resources (bv. afbeeldingen/iconen in `mipmap`) zijn volledig lowercase.
- Packagenamen zijn volledig lowercase, zonder hoofdletters of underscores (bv. `be.aidenstorme.todoapplication`, niet `be.aidenstorme.ToDoApplication`).
- Data classes (modellen, bv. `ToDoItem`) staan in een aparte package `models`.

## Composables
- Elke composable functie heeft de annotation `@Composable`.
- Elke composable functie heeft ALTIJD de parameter `modifier: Modifier = Modifier`, ook als die niet gebruikt wordt bij het aanroepen. Vergeet dit nooit.
- Geef die `modifier`-parameter altijd door aan het buitenste element van de composable (bv. `Column(modifier = modifier)`).
- Eigen composables zijn altijd stateless (state hoisting): waarden en callbacks (bv. `value` en `onValueChange`) komen binnen als parameters, nooit hardcoded of met interne state.
- Volgorde van parameters: eerst verplichte waarden, dan optionele parameters met een standaardwaarde (incl. `modifier: Modifier = Modifier`), en de callback (bv. `onValueChange`) als laatste, zodat de composable met trailing lambda aangeroepen kan worden: `AppTextField(value = uiState.name, label = stringResource(R.string.name)) { viewModel.onNameChange(it) }`.
- Gebruik voor tekst altijd een composable (`Text`).
- Gebruik GEEN `@Preview`-functies en geen Preview-imports. De app wordt getest in de emulator.
- Gebruik `Column` om elementen onder elkaar te plaatsen en `Row` om ze naast elkaar te plaatsen (behalve voor lijsten, zie Lijsten). Verdeel ruimte met gewichten (`Modifier.weight(...)`) via de modifier.

## Layout
- Gebruik `Scaffold` en geef de `innerPadding` door als padding aan de content, zodat niets achter de statusbalk terechtkomt.

## Lijsten
- Toon lijsten altijd met `LazyColumn`, nooit met een gewone `Column`.
- Gebruik `items(...)` om over de lijst te itereren.
- Elk item in de lijst wordt getoond in een `ElevatedCard` met een `modifier`.

## State
- De ViewModel is de enige eigenaar van de state van een view (single source of truth).
- De state van een view staat in een aparte data class (bv. `AddUserUiState`).
- In de ViewModel: een private `MutableStateFlow`, naar buiten enkel een read-only `StateFlow` als `val`.
- In de View observeer je de `StateFlow` via `collectAsState()`: `val uiState by viewModel.uiState.collectAsState()`.
- Events (bv. op save klikken) roepen een functie van de ViewModel aan. De view past de state nooit zelf aan.
- `rememberSaveable` enkel voor state die puur bij de UI van één component hoort (bv. open/dichtgeklapt van een kaart). Gebruik `by`, niet `=`: `var expanded by rememberSaveable { mutableStateOf(false) }`.

## Invoervelden (TextField)
- Geef een `TextField` altijd een `value` (de state-variabele uit `uiState`) en een `onValueChange` die de ViewModel bijwerkt (bv. `onValueChange = { viewModel.onNameChange(it) }`).
- Geef een label mee via `label = { Text(stringResource(R.string.…)) }`.
- Gebruik voor iconen (bv. `leadingIcon`) de Material Icons van Google. Voeg daarvoor altijd `androidx.compose.material:material-icons-core` toe via de version catalog (`gradle/libs.versions.toml` + `app/build.gradle.kts`). Importeer met een wildcard (bv. `import androidx.compose.material.icons.filled.*`) zodat alle iconen beschikbaar zijn.
- Heb je een icoon nodig dat niet in de basisset zit, voeg dan `androidx.compose.material:material-icons-extended` toe via de version catalog (`gradle/libs.versions.toml` + `app/build.gradle.kts`). Gebruik geen iconen die niet beschikbaar zijn in de toegevoegde dependencies.
- Decoratieve iconen (bv. een `leadingIcon`) krijgen `contentDescription = null`.
- Velden die enkel cijfers mogen bevatten krijgen `keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)`.
- Valideer numerieke invoer met de utility-functie: `onValueChange = { score = Utility.checkIntValue(it) ?: score }`, zodat de laatste geldige waarde behouden blijft.

## Utility-functies
- Hulpfuncties staan in een aparte package `utility`, in een `object` (geen `class`), zodat ze statisch aangeroepen worden: `Utility.checkIntValue(...)`.
- `checkIntValue(value: String): String?` werkt zo: lege string → `""` (zodat de gebruiker het veld kan leegmaken), geldige int via `value.toIntOrNull()` → `result.toString()`, anders → `null`.

## Resources
- Zet geen hardcoded teksten in de code. Alle teksten komen in `res/values/strings.xml` en worden opgehaald met `stringResource(R.string.…)`.

## Manifest
- Nodige permissions (camera, library, …) worden toegevoegd in `AndroidManifest.xml`.
- Nieuwe activities worden geregistreerd in `AndroidManifest.xml`.

## Dependencies
- Externe libraries worden toegevoegd in `app/build.gradle.kts` via de version catalog.
- Versies worden beheerd in `gradle/libs.versions.toml`, nooit rechtstreeks in `build.gradle.kts`.

## Werkwijze
- Maak bij grotere wijzigingen eerst een plan en wijzig geen bestanden tot ik bevestig met "proceed".
- Controleer na elke wijziging of het project compileert en los fouten op voor je zegt dat je klaar bent.
- Doe enkel wat gevraagd wordt. Voeg geen extra lagen toe (Room, repository, data layer, navigatie) als dat niet expliciet gevraagd is.

## Definition of Done
Een taak is pas klaar als:
- het project compileert (`./gradlew assembleDebug`) zonder fouten
- alle regels in dit bestand gevolgd zijn
- alle teksten in `res/values/strings.xml` staan
- er geen bestaande tests verwijderd of uitgeschakeld zijn om ze te laten slagen
- je een korte samenvatting gegeven hebt van welke bestanden je gewijzigd hebt
