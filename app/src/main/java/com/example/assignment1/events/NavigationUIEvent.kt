package com.example.assignment1.events

import androidx.navigation.NavController
import com.example.assignment1.Routes


sealed class NavigationUiEvent {
    data object NavigateToMainScreen : NavigationUiEvent()
    data object NavigateToImagePreviewScreen : NavigationUiEvent()
    data object NavigateToImageEditScreen : NavigationUiEvent()
    data object NavigateBack : NavigationUiEvent()
}

fun onNavigationEvent(event: NavigationUiEvent, navController: NavController) {
    when (event) {
        is NavigationUiEvent.NavigateToMainScreen -> {
            navController.navigate(Routes.MAINSCREEN)
        }

        is NavigationUiEvent.NavigateToImagePreviewScreen -> {
            navController.navigate(Routes.IMAGEPREVIEWSCREEN)
        }

        is NavigationUiEvent.NavigateToImageEditScreen -> {
            navController.navigate(Routes.IMAGEEDITSCREEN)
        }

        is NavigationUiEvent.NavigateBack -> {
            navController.navigateUp()
        }

    }
}