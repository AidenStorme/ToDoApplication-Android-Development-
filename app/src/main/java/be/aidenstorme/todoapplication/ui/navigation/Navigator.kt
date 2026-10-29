package be.aidenstorme.todoapplication.ui.navigation

import androidx.compose.runtime.mutableStateListOf

class Navigator(initialScreen: Screen = Screen.ToDoList) {
    val backStack = mutableStateListOf<Any>(initialScreen)

    fun navigate(screen: Screen) {
        backStack.add(screen)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
}
