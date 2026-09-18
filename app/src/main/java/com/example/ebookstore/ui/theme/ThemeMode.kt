package com.example.ebookstore.ui.theme

/**
 * Represents the three supported theme modes for the EBookStore app.
 *
 * - [SYSTEM]: Follows the device's system dark/light mode setting (default).
 * - [LIGHT]: Forces the light color scheme regardless of system setting.
 * - [DARK]: Forces the dark color scheme regardless of system setting.
 *
 * The user's selection is persisted via DataStore and read back on app launch
 * through [ThemeViewModel]. The value is stored as the enum name String.
 */
enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}
