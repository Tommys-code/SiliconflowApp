package com.tommy.siliconflow.app.extensions

import androidx.navigation.NavHostController

fun <T : Any> NavHostController.navigateAndPopBackStack(newRoute: T, popRoute: String? = null) {
    navigate(newRoute) {
        (popRoute ?: this@navigateAndPopBackStack.currentBackStackEntry?.destination?.route)?.let {
            popUpTo(it) { inclusive = true }
            launchSingleTop = true
        }
    }
}

fun <T : Any> NavHostController.navigateAndClearAll(route: T) {
    navigate(route) {
        popUpTo(0)
        launchSingleTop = true
    }
}
