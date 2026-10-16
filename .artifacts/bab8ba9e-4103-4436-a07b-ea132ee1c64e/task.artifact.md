# Tasks

- `[x]` Dependencies toevoegen
  - `[x]` Gebruik `version.ref = "lifecycleRuntimeKtx"` in `gradle/libs.versions.toml`
  - `[x]` Voeg `libs.androidx.lifecycle.viewmodel.compose` toe aan `app/build.gradle.kts`
  - `[x]` Gradle sync uitvoeren (`gradle_sync`)
- `[x]` ViewModels & UI States aanmaken (`ui/viewmodels`)
  - `[x]` `ToDoListUiState.kt` & `ToDoListViewModel.kt`
  - `[x]` `ToDoDetailUiState.kt` & `ToDoDetailViewModel.kt` (resets state direct bij `setToDo`)
  - `[x]` `ToDoEditUiState.kt` & `ToDoEditViewModel.kt` (resets state direct bij `setToDoToEdit` en bevat `onSave(onSaveCallback)`)
- `[x]` Views aanpassen (`ui/views` & `ui`)
  - `[x]` `ToDoListView.kt` aanpassen met `ToDoListViewModel`
  - `[x]` `ToDoDetailView.kt` aanpassen met `ToDoDetailViewModel`
  - `[x]` `ToDoEditView.kt` aanpassen met `ToDoEditViewModel` en `viewModel.onSave(onSave)`
  - `[x]` `ToDoApp.kt` bijwerken
- `[x]` Verificatie & Build
  - `[x]` Project bouwen (`gradle_build("assembleDebug")`)
  - `[x]` Unit tests uitvoeren (`gradle_build("test")`)
