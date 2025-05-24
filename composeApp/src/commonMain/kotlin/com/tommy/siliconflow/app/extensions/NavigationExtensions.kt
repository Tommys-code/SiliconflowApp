package com.tommy.siliconflow.app.extensions

import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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