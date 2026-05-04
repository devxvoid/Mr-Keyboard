package com.robot.mrkeyboard

import android.content.Context
import android.graphics.Typeface
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class TermuxKeyboardService : InputMethodService() {

    private var ctrlPressed = false
    private var altPressed = false
    private var shifted = false
    private var symbolsMode = false
    private var currentBuffer = ""

    private var ctrlButton: Button? = null
    private var altButton: Button? = null
    private var shiftButton: Button? = null

    private lateinit var prefs: KeyboardPrefs
    private lateinit var macros: MacroRepository
    private lateinit var predictionEngine: PredictionEngine
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        prefs = KeyboardPrefs(this)
        macros = MacroRepository()
        predictionEngine = PredictionEngine()
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    override fun onCreateInputView(): View {
        return buildKeyboard()
    }

    private fun buildKeyboard(): View {
        val theme = KeyboardTheme.fromName(prefs.themeName)

        val root = LinearLayout(this)
        root.orientation = LinearLayout.VERTICAL
        root.setPadding(dp(5), dp(5), dp(5), dp(5))
        root.setBackgroundColor(theme.background)

        val title = TextView(this)
        title.text = if (symbolsMode) "MR.ROBOT // SYMBOL LAYER" else "MR.ROBOT // TERMINAL KEYBOARD v5"
        title.setTextColor(theme.primary)
        title.textSize = 9.5f
        title.gravity = Gravity.CENTER_VERTICAL
        title.typeface = Typeface.MONOSPACE
        title.setPadding(dp(8), 0, dp(8), 0)
        root.addView(title, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            if (prefs.compactMode) dp(20) else dp(24)
        ))

        if (prefs.predictionHooksEnabled) buildSuggestionRow(root)

        buildToolRow(root)

        if (symbolsMode) {
            buildSymbolsLayer(root)
        } else {
            buildAlphaLayer(root)
        }

        buildActionRow(root)

        if (prefs.commandRowEnabled) buildCommandRow(root)
        if (prefs.macroRowEnabled) buildMacroRow(root)

        return root
    }

    private fun buildSuggestionRow(root: LinearLayout) {
        val suggestions = predictionEngine.suggestions(currentBuffer)
        if (suggestions.isEmpty()) return

        val r = row(34)
        suggestions.forEach { suggestion ->
            addKey(r, suggestion, 1f, primary = true) {
                replaceBufferWith(suggestion)
            }
        }
        root.addView(r)
    }

    private fun buildToolRow(root: LinearLayout) {
        val tools = row(if (prefs.compactMode) 34 else 38)
        addKey(tools, "ESC") { sendKey(KeyEvent.KEYCODE_ESCAPE) }
        addKey(tools, "TAB") { sendKey(KeyEvent.KEYCODE_TAB) }

        ctrlButton = addKey(tools, "CTRL") {
            ctrlPressed = !ctrlPressed
            ctrlButton?.isSelected = ctrlPressed
        }

        altButton = addKey(tools, "ALT") {
            altPressed = !altPressed
            altButton?.isSelected = altPressed
        }

        addKey(tools, "LEFT") { sendKey(KeyEvent.KEYCODE_DPAD_LEFT) }
        addKey(tools, "UP") { sendKey(KeyEvent.KEYCODE_DPAD_UP) }
        addKey(tools, "DOWN") { sendKey(KeyEvent.KEYCODE_DPAD_DOWN) }
        addKey(tools, "RIGHT") { sendKey(KeyEvent.KEYCODE_DPAD_RIGHT) }
        root.addView(tools)
    }

    private fun buildAlphaLayer(root: LinearLayout) {
        addTextRow(root, listOf("1","2","3","4","5","6","7","8","9","0"))
        addLetterRow(root, listOf("q","w","e","r","t","y","u","i","o","p"))
        addLetterRow(root, listOf("a","s","d","f","g","h","j","k","l"))

        val zrow = row(if (prefs.compactMode) 38 else 42)
        shiftButton = addKey(zrow, "SHIFT", 1.5f) {
            shifted = !shifted
            shiftButton?.isSelected = shifted
        }

        listOf("z","x","c","v","b","n","m").forEach { value ->
            addKey(zrow, value.uppercase()) { commitLetter(value[0]) }
        }

        addKey(zrow, "DEL", 1.5f) { backspace() }
        root.addView(zrow)

        val symbols = row(if (prefs.compactMode) 36 else 40)
        listOf("/", "|", "~", "$", "-", "_", ".", ":", "@").forEach { value ->
            addKey(symbols, value) { commitText(value) }
        }
        root.addView(symbols)
    }

    private fun buildSymbolsLayer(root: LinearLayout) {
        addTextRow(root, listOf("!","@","#","$","%","^","&","*","(",")"))
        addTextRow(root, listOf("-","_","+","=","[","]","{","}","/","?"))
        addTextRow(root, listOf("<",">","|","~","`","'",";",":",",","."))
        addTextRow(root, listOf("HOME","END","PGUP","PGDN","INS","FDEL"))

        val extra = row(if (prefs.compactMode) 36 else 40)
        addKey(extra, "HOME") { sendKey(KeyEvent.KEYCODE_MOVE_HOME) }
        addKey(extra, "END") { sendKey(KeyEvent.KEYCODE_MOVE_END) }
        addKey(extra, "PGUP") { sendKey(KeyEvent.KEYCODE_PAGE_UP) }
        addKey(extra, "PGDN") { sendKey(KeyEvent.KEYCODE_PAGE_DOWN) }
        root.addView(extra)
    }

    private fun buildActionRow(root: LinearLayout) {
        val action = row(if (prefs.compactMode) 38 else 42)
        addKey(action, if (symbolsMode) "ABC" else "SYM", 1.2f) {
            symbolsMode = !symbolsMode
            setInputView(buildKeyboard())
        }
        addKey(action, "CTRL+C", 1.35f) { sendCtrlKey(KeyEvent.KEYCODE_C) }
        addKey(action, "CTRL+D", 1.35f) { sendCtrlKey(KeyEvent.KEYCODE_D) }
        addKey(action, "SPACE", 4f) { commitText(" ") }
        addKey(action, "ENTER", 1.8f, primary = true) { sendKey(KeyEvent.KEYCODE_ENTER) }
        root.addView(action)
    }

    private fun buildCommandRow(root: LinearLayout) {
        val commands = row(if (prefs.compactMode) 36 else 40)
        addKey(commands, "ls") { commitText("ls ") }
        addKey(commands, "cd") { commitText("cd ") }
        addKey(commands, "pwd") { commitText("pwd") }
        addKey(commands, "clear") {
            commitText("clear")
            sendKey(KeyEvent.KEYCODE_ENTER)
        }
        addKey(commands, "pkg update", 2f) {
            commitText("pkg update && pkg upgrade")
        }
        root.addView(commands)
    }

    private fun buildMacroRow(root: LinearLayout) {
        val macroRow = row(if (prefs.compactMode) 36 else 40)
        macros.defaultMacros().take(4).forEach { macro ->
            addKey(macroRow, macro.label, 1f, primary = true) {
                commitText(macro.command)
            }
        }
        root.addView(macroRow)
    }

    private fun addTextRow(root: LinearLayout, items: List<String>) {
        val r = row(if (prefs.compactMode) 36 else 40)
        items.forEach { value ->
            when (value) {
                "HOME" -> addKey(r, value) { sendKey(KeyEvent.KEYCODE_MOVE_HOME) }
                "END" -> addKey(r, value) { sendKey(KeyEvent.KEYCODE_MOVE_END) }
                "PGUP" -> addKey(r, value) { sendKey(KeyEvent.KEYCODE_PAGE_UP) }
                "PGDN" -> addKey(r, value) { sendKey(KeyEvent.KEYCODE_PAGE_DOWN) }
                "INS" -> addKey(r, value) { sendKey(KeyEvent.KEYCODE_INSERT) }
                "FDEL" -> addKey(r, value) { sendKey(KeyEvent.KEYCODE_FORWARD_DEL) }
                else -> addKey(r, value) { commitText(value) }
            }
        }
        root.addView(r)
    }

    private fun addLetterRow(root: LinearLayout, items: List<String>) {
        val r = row(if (prefs.compactMode) 38 else 42)
        items.forEach { value ->
            addKey(r, value.uppercase()) { commitLetter(value[0]) }
        }
        root.addView(r)
    }

    private fun row(height: Int): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.setPadding(0, dp(1), 0, dp(1))
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(height)
        )
        return layout
    }

    private fun addKey(
        row: LinearLayout,
        label: String,
        weight: Float = 1f,
        primary: Boolean = false,
        action: () -> Unit
    ): Button {
        val theme = KeyboardTheme.fromName(prefs.themeName)
        val button = Button(this)
        button.text = label
        button.setTextColor(theme.primary)
        button.textSize = if (label.length > 6) 8.0f else 9.5f
        button.typeface = Typeface.MONOSPACE
        button.isAllCaps = false
        button.setPadding(0, 0, 0, 0)
        button.setBackgroundResource(if (primary) R.drawable.key_primary else R.drawable.key_hacker)
        button.setOnLongClickListener {
            showPreview(label)
            true
        }
        button.setOnClickListener {
            feedback()
            action()
        }

        val params = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
            weight
        )
        params.setMargins(dp(2), dp(1), dp(2), dp(1))
        row.addView(button, params)
        return button
    }

    private fun commitLetter(char: Char) {
        val keyCode = keyCodeForLetter(char)

        if (ctrlPressed && keyCode != null) {
            sendKey(keyCode)
            return
        }

        val output = if (shifted) char.uppercaseChar().toString() else char.lowercaseChar().toString()
        commitText(output)

        if (shifted) {
            shifted = false
            shiftButton?.isSelected = false
        }
    }

    private fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
        updateBuffer(text)
        if (prefs.predictionHooksEnabled) {
            setInputView(buildKeyboard())
        }
    }

    private fun replaceBufferWith(text: String) {
        val ic = currentInputConnection ?: return
        if (currentBuffer.isNotEmpty()) {
            ic.deleteSurroundingText(currentBuffer.length, 0)
        }
        ic.commitText(text, 1)
        currentBuffer = text
        setInputView(buildKeyboard())
    }

    private fun updateBuffer(text: String) {
        if (text.any { it.isWhitespace() || it == '\\n' }) {
            currentBuffer = ""
        } else if (text.length <= 24) {
            currentBuffer += text
            if (currentBuffer.length > 24) {
                currentBuffer = currentBuffer.takeLast(24)
            }
        }
    }

    private fun backspace() {
        val ic = currentInputConnection ?: return
        ic.deleteSurroundingText(1, 0)
        if (currentBuffer.isNotEmpty()) currentBuffer = currentBuffer.dropLast(1)
        if (prefs.predictionHooksEnabled) setInputView(buildKeyboard())
    }

    private fun sendKey(keyCode: Int) {
        val ic: InputConnection = currentInputConnection ?: return
        var meta = 0
        if (ctrlPressed) meta = meta or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        if (altPressed) meta = meta or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON

        ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0, meta))
        ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, meta))

        if (keyCode == KeyEvent.KEYCODE_ENTER) currentBuffer = ""
        resetModifiers()
    }

    private fun sendCtrlKey(keyCode: Int) {
        val ic = currentInputConnection ?: return
        val meta = KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0, meta))
        ic.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, meta))
        resetModifiers()
    }

    private fun feedback() {
        if (!prefs.vibrationEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(12)
            }
        } catch (_: Exception) {
        }
    }

    private fun showPreview(label: String) {
        if (!prefs.keyPreviewEnabled) return
        Toast.makeText(this, label, Toast.LENGTH_SHORT).show()
    }

    private fun resetModifiers() {
        ctrlPressed = false
        altPressed = false
        ctrlButton?.isSelected = false
        altButton?.isSelected = false
    }

    private fun keyCodeForLetter(char: Char): Int? {
        return when (char.lowercaseChar()) {
            'a' -> KeyEvent.KEYCODE_A
            'b' -> KeyEvent.KEYCODE_B
            'c' -> KeyEvent.KEYCODE_C
            'd' -> KeyEvent.KEYCODE_D
            'e' -> KeyEvent.KEYCODE_E
            'f' -> KeyEvent.KEYCODE_F
            'g' -> KeyEvent.KEYCODE_G
            'h' -> KeyEvent.KEYCODE_H
            'i' -> KeyEvent.KEYCODE_I
            'j' -> KeyEvent.KEYCODE_J
            'k' -> KeyEvent.KEYCODE_K
            'l' -> KeyEvent.KEYCODE_L
            'm' -> KeyEvent.KEYCODE_M
            'n' -> KeyEvent.KEYCODE_N
            'o' -> KeyEvent.KEYCODE_O
            'p' -> KeyEvent.KEYCODE_P
            'q' -> KeyEvent.KEYCODE_Q
            'r' -> KeyEvent.KEYCODE_R
            's' -> KeyEvent.KEYCODE_S
            't' -> KeyEvent.KEYCODE_T
            'u' -> KeyEvent.KEYCODE_U
            'v' -> KeyEvent.KEYCODE_V
            'w' -> KeyEvent.KEYCODE_W
            'x' -> KeyEvent.KEYCODE_X
            'y' -> KeyEvent.KEYCODE_Y
            'z' -> KeyEvent.KEYCODE_Z
            else -> null
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
