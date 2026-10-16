# Bijgewerkt Implementatieplan: ViewModels & UI States

In dit bijgewerkte plan adresseren we de specifieke punten:
1. **Verwerking van de geselecteerde ToDo in `ToDoDetailViewModel` en `ToDoEditViewModel`**: Garanderen dat bij het openen van een (nieuwe/andere) ToDo nooit gegevens van een eerder geopende ToDo getoond worden.
2. **Vervanging van `getSaveableToDo()` door `onSave()` in `ToDoEditViewModel`**: De ViewModel verwerkt de bewaaractie rechtstreeks via een `onSave()` functie.
3. **Hergebruik van de bestaande `lifecycleRuntimeKtx` version.ref**: In `libs.versions.toml` gebruiken we de bestaande `version.ref = "lifecycleRuntimeKtx"` voor `androidx-lifecycle-viewmodel-compose`.

---

## User Review Required

> [!IMPORTANT]
> 1. **Doorgeven van geselecteerde `ToDo`**:
>    - `ToDoDetailView` en `ToDoEditView` gebruiken `LaunchedEffect(toDo)` / `LaunchedEffect(toDoToEdit)` om bij elke verandering van het geselecteerde item direct `viewModel.setToDo(toDo)` / `viewModel.setToDoToEdit(toDoToEdit)` aan te roepen.
>    - In de ViewModels wist `setToDo` / `setToDoToEdit` onmiddellijk de vorige state en stelt de nieuwe waarden in. Zo wordt gegarandeerd dat nooit een eerder geopende ToDo getoond wordt.
>
> 2. **`onSave()` in ViewModel**:
>    - `getSaveableToDo()` in `ToDoEditViewModel` wordt vervangen door `fun onSave(onSaveCallback: (ToDo) -> Unit)`. De View roept enkel `viewModel.onSave(onSave)` aan.
>
> 3. **Hergebruik `version.ref = "lifecycleRuntimeKtx"`**:
>    - `libs.versions.toml` gebruikt `version.ref = "lifecycleRuntimeKtx"` voor `lifecycle-viewmodel-compose`.

---

## Proposed Changes

### Build System & Dependencies

#### [MODIFY] [libs.versions.toml](file:///Users/aidenstorme/Documents/Android%20Development/ToDo%20Application/gradle/libs.versions.toml)
- Gebruik `version.ref = "lifecycleRuntimeKtx"` voor `androidx-lifecycle-viewmodel-compose` en verwijder `lifecycleViewmodelCompose`.

---

### ViewModels & UI States (`ui/viewmodels`)

#### [MODIFY] [ToDoEditViewModel.kt](file:///Users/aidenstorme/Documents/Android%20Development/ToDo%20Application/app/src/main/java/be/aidenstorme/todoapplication/ui/viewmodels/ToDoEditViewModel.kt)
- Vervang `getSaveableToDo(): ToDo` door `fun onSave(onSaveCallback: (ToDo) -> Unit)`.
- Zorg dat `setToDoToEdit(toDo: ToDo?)` de volledige state onmiddellijk overschrijft/reset bij het aanroepen.

#### [MODIFY] [ToDoDetailViewModel.kt](file:///Users/aidenstorme/Documents/Android%20Development/ToDo%20Application/app/src/main/java/be/aidenstorme/todoapplication/ui/viewmodels/ToDoDetailViewModel.kt)
- Zorg dat `setToDo(toDo: ToDo)` de state onmiddellijk overschrijft bij het aanroepen.

---

### Views (`ui/views`)

#### [MODIFY] [ToDoEditView.kt](file:///Users/aidenstorme/Documents/Android%20Development/ToDo%20Application/app/src/main/java/be/aidenstorme/todoapplication/ui/views/ToDoEditView.kt)
- Bij het op Save klikken, roep `viewModel.onSave(onSave)` aan.

---

## Verification Plan

### Automated Tests
- Uitvoeren van `./gradlew assembleDebug` om te verifiëren dat de applicatie compileert.
- Uitvoeren van `./gradlew test` om unit tests te controleren.
