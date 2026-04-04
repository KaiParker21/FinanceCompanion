package com.skye.financecompanion.presentation.navigation

sealed class RootScreen(val route: String) {
    object Splash : RootScreen("splash")
    object Login : RootScreen("login")
    object Register : RootScreen("register")
    object MainApp : RootScreen("main_app")
}