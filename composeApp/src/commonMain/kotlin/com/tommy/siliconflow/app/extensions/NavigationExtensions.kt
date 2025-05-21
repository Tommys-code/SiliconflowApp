package com.tommy.siliconflow.app.extensions

import androidx.navigation.NavHostController

fun NavHostController.navigateAndPopBackStack(newRoute: String, popRoute: String? = null) {
    navigate(newRoute) {
        (popRoute ?: this@navigateAndPopBackStack.currentBackStackEntry?.destination?.route)?.let {
            popUpTo(it) { inclusive = true }
            launchSingleTop = true
        }
    }
}

fun NavHostController.navigateAndClearAll(route: String) {
    navigate(route) {
        popUpTo(0)
        launchSingleTop = true
    }
}