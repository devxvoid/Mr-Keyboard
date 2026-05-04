package com.robot.mrkeyboard

import android.graphics.Color

data class KeyboardTheme(
    val name: String,
    val background: Int,
    val panel: Int,
    val primary: Int,
    val secondary: Int,
    val muted: Int
) {
    companion object {
        val green = KeyboardTheme(
            name = "Green Terminal",
            background = Color.rgb(3, 6, 5),
            panel = Color.rgb(8, 16, 12),
            primary = Color.rgb(0, 255, 102),
            secondary = Color.rgb(0, 190, 80),
            muted = Color.rgb(90, 150, 105)
        )

        val red = KeyboardTheme(
            name = "Red Alert",
            background = Color.rgb(8, 3, 3),
            panel = Color.rgb(18, 8, 8),
            primary = Color.rgb(255, 58, 58),
            secondary = Color.rgb(210, 40, 40),
            muted = Color.rgb(170, 100, 100)
        )

        val blue = KeyboardTheme(
            name = "Blue Matrix",
            background = Color.rgb(3, 5, 10),
            panel = Color.rgb(7, 12, 22),
            primary = Color.rgb(80, 190, 255),
            secondary = Color.rgb(50, 140, 220),
            muted = Color.rgb(110, 150, 190)
        )

        fun fromName(name: String): KeyboardTheme {
            return when (name) {
                red.name -> red
                blue.name -> blue
                else -> green
            }
        }
    }
}
