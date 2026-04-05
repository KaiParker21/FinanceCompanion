package com.skye.financecompanion.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.skye.financecompanion.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authState: AuthState, // 1. Pass the state in
    onNavigateToMain: () -> Unit, // 2. Lambda for logged-in users
    onNavigateToLogin: () -> Unit // 3. Lambda for logged-out users
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_animation))

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(250.dp)
        )
    }

    // Wait for the animation to hit 100% (1f)
    LaunchedEffect(progress) {
        if (progress == 1f) {
            delay(300) // A tiny pause so the animation doesn't vanish too abruptly

            // Check the AuthState and route accordingly!
            if (authState is AuthState.Authenticated) {
                onNavigateToMain()
            } else {
                onNavigateToLogin()
            }
        }
    }
}