package com.skye.financecompanion.presentation.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    authState: AuthState,
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val scale = remember { Animatable(0f) }

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.background
        )
    )

    LaunchedEffect(key1 = true) {
        // 1. Animate the logo popping in
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        // 2. Wait just a moment so the user can enjoy the splash screen
        delay(600)

        // 3. Decide where to go based on Firebase Auth State
        if (authState is AuthState.Authenticated) {
            onNavigateToMain()
        } else {
            onNavigateToLogin()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(100.dp)
                .scale(scale.value), // Apply the animation
            shadowElevation = 12.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("💸", style = MaterialTheme.typography.displayLarge)
            }
        }
    }
}