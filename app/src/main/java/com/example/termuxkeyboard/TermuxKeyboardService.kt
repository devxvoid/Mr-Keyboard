package com.example.termuxkeyboard

import android.inputmethodservice.InputMethodService
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.Button

class TermuxKeyboardService : InputMethodService() {

    private var ctrlPressed = false
    private var altPressed = false
    private var ctrlButton: Button? = null
    private var altButton: Button? = null

    override fun onCreateInputView(): View {
        val view = layoutInflater.inflate(R.layout.keyboard_view, null)

        ctrlButton = view.findViewById(R.id.keyCtrl)
        altButton = view.findViewById(R.id.keyAlt)

        view.findViewById<Button>(R.id.keyEsc).setOnClickListener { sendKey(KeyEvent.KEYCODE_ESCAPE) }
        view.findViewById<Button>(R.id.keyTab).setOnClickListener { sendKey(KeyEvent.KEYCODE_TAB) }

        ctrlButton?.setOnClickListener {
            ctrlPressed = !ctrlPressed
            it.isSelected = ctrlPressed
        }

        altButton?.setOnClickListener {
            altPressed = !altPressed
            it.isSelected = altPressed
        }

        view.findViewById<Button>(R.id.keySlash).setOnClickListener { commitText("/") }
        view.findViewById<Button>(R.id.keyPipe).setOnClickListener { commitText("|") }
        view.findViewById<Button>(R.id.keyTilde).setOnClickListener { commitText("~") }
        view.findViewById<Button>(R.id.keyDollar).setOnClickListener { commitText("$") }

        view.findViewById<Button>(R.id.keyLeft).setOnClickListener { sendKey(KeyEvent.KEYCODE_DPAD_LEFT) }
        view.findViewById<Button>(R.id.keyRight).setOnClickListener { sendKey(KeyEvent.KEYCODE_DPAD_RIGHT) }
        view.findViewById<Button>(R.id.keyUp).setOnClickListener { sendKey(KeyEvent.KEYCODE_DPAD_UP) }
        view.findViewById<Button>(R.id.keyDown).setOnClickListener { sendKey(KeyEvent.KEYCODE_DPAD_DOWN) }
        view.findViewById<Button>(R.id.keyEnter).setOnClickListener { sendKey(KeyEvent.KEYCODE_ENTER) }

        view.findViewById<Button>(R.id.keyCtrlC).setOnClickListener { sendCtrlKey(KeyEvent.KEYCODE_C) }
        view.findViewById<Button>(R.id.keyCtrlD).setOnClickListener { sendCtrlKey(KeyEvent.KEYCODE_D) }
        view.findViewById<Button>(R.id.keyCtrlL).setOnClickListener { sendCtrlKey(KeyEvent.KEYCODE_L) }

        view.findViewById<Button>(R.id.keyLs).setOnClickListener { commitText("ls ") }
        view.findViewById<Button>(R.id.keyCd).setOnClickListener { commitText("cd ") }

        view.findViewById<Button>(R.id.keyClear).setOnClickListener {
            commitText("clear")
            sendKey(KeyEvent.KEYCODE_ENTER)
        }

        view.findViewById<Button>(R.id.keyPkg).setOnClickListener {
            commitText("pkg update && pkg upgrade")
        }

        return view
    }

    private fun commitText(text: String) {
        currentInputConnection?.commitText(text, 1)
    }

    private fun sendKey(keyCode: Int) {
        val inputConnection: InputConnection = currentInputConnection ?: return
        var metaState = 0

        if (ctrlPressed) {
            metaState = metaState or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        }

        if (altPressed) {
            metaState = metaState or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
        }

        inputConnection.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0, metaState))
        inputConnection.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, metaState))

        resetModifiers()
    }

    private fun sendCtrlKey(keyCode: Int) {
        val inputConnection = currentInputConnection ?: return
        val metaState = KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON

        inputConnection.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_DOWN, keyCode, 0, metaState))
        inputConnection.sendKeyEvent(KeyEvent(0, 0, KeyEvent.ACTION_UP, keyCode, 0, metaState))

        resetModifiers()
    }

    private fun resetModifiers() {
        ctrlPressed = false
        altPressed = false
        ctrlButton?.isSelected = false
        altButton?.isSelected = false
    }
}
