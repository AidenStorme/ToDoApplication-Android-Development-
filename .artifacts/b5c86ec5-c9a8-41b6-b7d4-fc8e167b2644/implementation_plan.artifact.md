# Network Layer Implementation Plan for Bored API

## Problem & Background
The user wants to access `https://bored-api.appbrewery.com/random` to fetch random activities in the ToDo application. We need to design and plan a clean network layer adhering to project constraints (no new libraries added without asking, Kotlin, kotlinx.serialization, ViewModel, StateFlow, etc.).

## User Review Required

> [!IMPORTANT]
> **HTTP Client Choice**:
> 1. **Option A (No new dependencies)**: Use standard `HttpURLConnection` combined with `kotlinx.serialization` (which is already in the project dependencies). This requires zero external library additions and complies strictly with the rule "Voeg geen nieuwe libraries toe zonder het eerst te vragen".
> 2. **Option B (Ktor or Retrofit)**: Add Ktor or Retrofit to `libs.versions.toml` and `app/build.gradle.kts`. This would require explicit user confirmation to add new libraries.
>
> *Recommendation*: Option A (`HttpURLConnection` + `kotlinx.serialization`) or asking the user if they prefer Ktor/Retrofit. We will plan for Option A as the default zero-dependency approach, but highlight it for review.

> [!IMPORTANT]
> **Internet Permission**: We must ensure `<uses-permission android:name="android.permission.INTERNET" />` is present in `AndroidManifest.xml`.

## Open Questions
- Should the fetched random activity be mapped to our existing `ToDo` model or a dedicated API model?
- Should we add a UI screen or ViewModel action to fetch and display a random activity from the API?

## Proposed Changes

### [Network & Models]

#### [NEW] [BoredApiService.kt](file:///Users/aidenstorme/Documents/Android Development/ToDo Application/app/src/main/java/be/aidenstorme/todoapplication/data/BoredApiService.kt)
- Network service utility using `HttpURLConnection` or coroutines (`withContext(Dispatchers.IO)`) to fetch JSON from `https://bored-api.appbrewery.com/random`.

#### [NEW] [BoredActivityResponse.kt](file:///Users/aidenstorme/Documents/Android Development/ToDo Application/app/src/main/java/be/aidenstorme/todoapplication/models/BoredActivityResponse.kt)
- `@Serializable` data class matching the Bored API JSON response (`activity`, `type`, `participants`, `price`, `link`, `key`, `accessibility`).

#### [MODIFY] [AndroidManifest.xml](file:///Users/aidenstorme/Documents/Android Development/ToDo Application/app/src/main/AndroidManifest.xml)
- Add `android.permission.INTERNET`.

## Verification Plan

### Automated Tests
- Unit test (`BoredApiServiceTest.kt` or similar) mocking or testing network parsing/service.
- `./gradlew assembleDebug` build verification.

### Manual Verification
- Deploy to emulator and test fetching a random activity.
