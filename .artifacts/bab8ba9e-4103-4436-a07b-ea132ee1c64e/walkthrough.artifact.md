# Walkthrough: Bijgewerkte ViewModels & UI States

Alle 3 de schermen (`ToDoListView`, `ToDoDetailView` en `ToDoEditView`) zijn aangepast naar de MVVM-architectuur met `ViewModel` en `UiState` dataklassen.

## Belangrijkste updates

1. **Afhandeling van de geselecteerde `ToDo` in `ToDoDetailViewModel` & `ToDoEditViewModel`**:
   - `setToDo(toDo: ToDo)` in `ToDoDetailViewModel` en `setToDoToEdit(toDo: ToDo?)` in `ToDoEditViewModel` overschrijven de `_uiState.value` onmiddellijk en volledig wanneer een item geopend wordt. Dit gegarandeerd dat oude gegevens van een eerder geopende `ToDo` nooit getoond worden.

2. **Vervanging van `getSaveableToDo()` door `onSave()`**:
   - In `ToDoEditViewModel` is `getSaveableToDo()` vervangen door `fun onSave(onSaveCallback: (ToDo) -> Unit)`. `ToDoEditView` roept bij het opslaan `viewModel.onSave(onSave)` aan.

3. **Hergebruik van de `lifecycleRuntimeKtx` versie-referentie**:
   - `libs.versions.toml` gebruikt `version.ref = "lifecycleRuntimeKtx"` voor `androidx-lifecycle-viewmodel-compose`.

---

## Verificatieresultaten

- **Compilation**: `./gradlew assembleDebug` is succesvol geslaagd zonder fouten.
- **Unit Tests**: `./gradlew testDebugUnitTest` is geslaagd (8 passed, 0 failed).
