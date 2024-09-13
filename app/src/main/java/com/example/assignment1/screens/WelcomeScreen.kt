package com.example.assignment1.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.assignment1.Routes
import kotlinx.coroutines.delay


@Composable
fun WelcomeScreen(navController: NavController) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        visible = false
        delay(300)
        navController.navigate(Routes.MAINSCREEN) {
            popUpTo(Routes.WELCOMESCREEN) { inclusive = true }
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(initialOffsetY = { it / 2 }), // + slideInHorizontally(initialOffsetX = { it / 2 }),
        exit = fadeOut(animationSpec = tween(500)) + slideOutVertically(targetOffsetY = { -it / 2 }) // + slideOutHorizontally(targetOffsetX = { -it / 2 })
    ) {
        Welcome()
    }
}

@Composable
fun Welcome() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Image", style = typography.displayLarge, fontStyle = FontStyle.Italic)
        Text(text = "Manipulation", style = typography.displayLarge, fontStyle = FontStyle.Italic)
        Text(text = "Application", style = typography.displayLarge, fontStyle = FontStyle.Italic)

    }

}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    Welcome()
}