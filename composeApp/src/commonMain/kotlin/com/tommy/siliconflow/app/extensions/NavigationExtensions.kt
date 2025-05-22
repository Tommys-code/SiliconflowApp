package com.tommy.siliconflow.app.extensions

import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

private var isBackPressed = false
fun NavHostController.safePopBackStack(scope: CoroutineScope) {
    if (!isBackPressed) {
        isBackPressed = true
        popBackStack()
        scope.launch {
            delay(500)
            isBackPressed = false
        }
    }
}