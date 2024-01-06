package com.example.notes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.notes.ui.navigation.NoteNavGraph

@Composable
fun NoteApp(navController: NavHostController = rememberNavController()) {
    NoteNavGraph(navController = navController)
}