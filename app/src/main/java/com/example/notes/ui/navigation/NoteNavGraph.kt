package com.example.notes.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.notes.ui.add_edit.AddEditNoteScreen
import com.example.notes.ui.home.HomeScreen
import com.example.notes.ui.search.SearchScreen

@Composable
fun NoteNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
        modifier = modifier
    ) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                onNoteClick = { navController.navigate(Screen.AddEditScreen.navWithArg(it)) },
                onCreateNewNote = { navController.navigate(Screen.AddEditScreen.navWithArg(-1)) },
                onSearch = { navController.navigate(Screen.SearchScreen.route) }
            )
        }
        composable(
            route = Screen.AddEditScreen.route,
            arguments = listOf(navArgument(Screen.AddEditScreen.argumentKey) {
                type = NavType.IntType
            })
        ) {
            AddEditNoteScreen(
                navigateUp = { navController.navigateUp() }
            )
        }
        composable(route = Screen.SearchScreen.route) {
            SearchScreen(
                onNavigateUp = {navController.navigateUp() },
                onNoteClick = { navController.navigate(Screen.AddEditScreen.navWithArg(it)) }
            )
        }
    }
}