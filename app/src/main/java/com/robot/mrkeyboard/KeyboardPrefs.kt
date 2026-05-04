package com.robot.mrkeyboard

import android.content.Context

class KeyboardPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("mrrobot_keyboard_settings", Context.MODE_PRIVATE)

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean("vibration_enabled", true)
        set(value) = prefs.edit().putBoolean("vibration_enabled", value).apply()

    var compactMode: Boolean
        get() = prefs.getBoolean("compact_mode", false)
        set(value) = prefs.edit().putBoolean("compact_mode", value).apply()

    var commandRowEnabled: Boolean
        get() = prefs.getBoolean("command_row_enabled", true)
        set(value) = prefs.edit().putBoolean("command_row_enabled", value).apply()

    var macroRowEnabled: Boolean
        get() = prefs.getBoolean("macro_row_enabled", true)
        set(value) = prefs.edit().putBoolean("macro_row_enabled", value).apply()

    var keyPreviewEnabled: Boolean
        get() = prefs.getBoolean("key_preview_enabled", true)
        set(value) = prefs.edit().putBoolean("key_preview_enabled", value).apply()

    var predictionHooksEnabled: Boolean
        get() = prefs.getBoolean("prediction_hooks_enabled", false)
        set(value) = prefs.edit().putBoolean("prediction_hooks_enabled", value).apply()

    var themeName: String
        get() = prefs.getString("theme_name", KeyboardTheme.green.name) ?: KeyboardTheme.green.name
        set(value) = prefs.edit().putString("theme_name", value).apply()
}
