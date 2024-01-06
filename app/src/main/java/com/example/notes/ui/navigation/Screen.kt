package com.example.notes.ui.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home")
    object AddEditScreen: Screen("add_edit/{itemId}") {
        fun navWithArg(itemId: Int) ="add_edit/$itemId"
        const val argumentKey = "itemId"
    }
    object SearchScreen : Screen("search")
}