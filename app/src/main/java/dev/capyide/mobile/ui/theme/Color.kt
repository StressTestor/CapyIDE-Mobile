// Copyright 2025 CapyIDE Mobile. All rights reserved.
// Use of this source code is governed by a BSD-style
// license that can be found in the LICENSE file.

package dev.capyide.mobile.ui.theme

import androidx.compose.ui.graphics.Color

// CAPY Brand Colors - Extracted from logo (purple bg, capy in hoodie, white text)
val CapyPurple = Color(0xFF585F9B)   // main brand purple (background)
val CapyDark = Color(0xFF282829)     // hoodie / outline dark neutral
val CapyFur = Color(0xFFCB8A59)      // primary fur tone
val CapyFurDeep = Color(0xFFAD6A3D)  // darker fur / shading
val CapyWhite = Color(0xFFFFFFFF)    // text / highlights

// Material 3 Light Theme Colors
val CapyPrimary = CapyPurple
val CapyOnPrimary = CapyWhite
val CapySecondary = CapyFur
val CapyOnSecondary = CapyDark
val CapyBackground = CapyPurple
val CapyOnBackground = CapyWhite
val CapySurface = CapyDark
val CapyOnSurface = CapyWhite
val CapyTertiary = CapyFurDeep

// Legacy colors (for compatibility during transition)
val Purple80 = CapyPrimary
val PurpleGrey80 = CapySecondary
val Pink80 = CapyTertiary
val Purple40 = CapyPrimary
val PurpleGrey40 = CapySecondary
val Pink40 = CapyTertiary