package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = ForestGreenPrimaryDark,
    onPrimary = ForestGreenOnPrimaryDark,
    primaryContainer = ForestGreenContainerDark,
    onPrimaryContainer = ForestGreenOnContainerDark,
    secondary = LightGreenSecondaryDark,
    onSecondary = LightGreenOnSecondaryDark,
    secondaryContainer = LightGreenContainerDark,
    onSecondaryContainer = LightGreenOnContainerDark,
    tertiary = SunshineYellowTertiaryDark,
    onTertiary = SunshineYellowOnTertiaryDark,
    tertiaryContainer = SunshineYellowContainerDark,
    onTertiaryContainer = SunshineYellowOnContainerDark,
    background = EarthBackgroundDark,
    surface = EarthSurfaceDark,
    onBackground = EarthOnBackgroundDark,
    onSurface = EarthOnSurfaceDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = ForestGreenOnPrimary,
    primaryContainer = ForestGreenContainer,
    onPrimaryContainer = ForestGreenOnContainer,
    secondary = LightGreenSecondary,
    onSecondary = LightGreenOnSecondary,
    secondaryContainer = LightGreenContainer,
    onSecondaryContainer = LightGreenOnContainer,
    tertiary = SunshineYellowTertiary,
    onTertiary = SunshineYellowOnTertiary,
    tertiaryContainer = SunshineYellowContainer,
    onTertiaryContainer = SunshineYellowOnContainer,
    background = EarthBackgroundLight,
    surface = EarthSurfaceLight,
    onBackground = EarthOnBackgroundLight,
    onSurface = EarthOnSurfaceLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        try {
          if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } catch (e: Throwable) {
          if (darkTheme) DarkColorScheme else LightColorScheme
        }
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
