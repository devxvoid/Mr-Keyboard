package com.robot.mrkeyboard

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView

class SettingsActivity : Activity() {

    private lateinit var prefs: KeyboardPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = KeyboardPrefs(this)
        render()
    }

    private fun render() {
        val theme = KeyboardTheme.fromName(prefs.themeName)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(dp(18), dp(24), dp(18), dp(18))
        root.setBackgroundColor(theme.background)

        val title = TextView(this)
        title.text = "MR.ROBOT KEYBOARD v5"
        title.textSize = 24f
        title.setTextColor(theme.primary)
        title.typeface = Typeface.MONOSPACE
        title.gravity = Gravity.CENTER_HORIZONTAL
        root.addView(title)

        val subtitle = TextView(this)
        subtitle.text = "Terminal keyboard platform for Termux"
        subtitle.textSize = 13f
        subtitle.setTextColor(theme.secondary)
        subtitle.typeface = Typeface.MONOSPACE
        subtitle.gravity = Gravity.CENTER_HORIZONTAL
        subtitle.setPadding(0, dp(8), 0, dp(24))
        root.addView(subtitle)

        root.addView(settingSwitch("Vibration feedback", prefs.vibrationEnabled) { prefs.vibrationEnabled = it })
        root.addView(settingSwitch("Compact keyboard height", prefs.compactMode) { prefs.compactMode = it })
        root.addView(settingSwitch("Show command row", prefs.commandRowEnabled) { prefs.commandRowEnabled = it })
        root.addView(settingSwitch("Show macro row", prefs.macroRowEnabled) { prefs.macroRowEnabled = it })
        root.addView(settingSwitch("Key preview hook", prefs.keyPreviewEnabled) { prefs.keyPreviewEnabled = it })
        root.addView(settingSwitch("Prediction hook", prefs.predictionHooksEnabled) { prefs.predictionHooksEnabled = it })

        root.addView(actionButton("Theme: Green Terminal") {
            prefs.themeName = KeyboardTheme.green.name
            render()
        })

        root.addView(actionButton("Theme: Red Alert") {
            prefs.themeName = KeyboardTheme.red.name
            render()
        })

        root.addView(actionButton("Theme: Blue Matrix") {
            prefs.themeName = KeyboardTheme.blue.name
            render()
        })

        root.addView(actionButton("Open keyboard settings") {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        })

        root.addView(actionButton("Switch current keyboard") {
            @Suppress("DEPRECATION")
            (getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager)
                .showInputMethodPicker()
        })

        val info = TextView(this)
        info.text = "Enable MrRobot Keyboard, then select it from the keyboard picker."
        info.textSize = 12f
        info.setTextColor(theme.muted)
        info.typeface = Typeface.MONOSPACE
        info.setPadding(0, dp(20), 0, 0)
        root.addView(info)

        setContentView(root)
    }

    private fun settingSwitch(title: String, checked: Boolean, onChange: (Boolean) -> Unit): LinearLayout {
        val theme = KeyboardTheme.fromName(prefs.themeName)

        val row = LinearLayout(this)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(dp(12), dp(10), dp(12), dp(10))
        row.setBackgroundColor(theme.panel)

        val label = TextView(this)
        label.text = title
        label.textSize = 14f
        label.typeface = Typeface.MONOSPACE
        label.setTextColor(theme.primary)

        val sw = Switch(this)
        sw.isChecked = checked
        sw.setOnCheckedChangeListener { _, isChecked -> onChange(isChecked) }

        row.addView(label, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        row.addView(sw)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, dp(6), 0, dp(6))
        row.layoutParams = params

        return row
    }

    private fun actionButton(text: String, action: () -> Unit): Button {
        val theme = KeyboardTheme.fromName(prefs.themeName)

        val button = Button(this)
        button.text = text
        button.textSize = 13f
        button.typeface = Typeface.MONOSPACE
        button.setTextColor(theme.primary)
        button.setBackgroundResource(R.drawable.key_primary)
        button.isAllCaps = false
        button.setOnClickListener { action() }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(52)
        )
        params.setMargins(0, dp(10), 0, 0)
        button.layoutParams = params
        return button
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
