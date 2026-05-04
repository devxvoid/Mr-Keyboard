# NEXT UPGRADE: v6 Roadmap

This ZIP already includes v5 with production-level structure and upgrade hooks.

## v6 Planned Upgrade

### 1. Real Macro Editor
Current v5 has MacroRepository with default built-in macros.
Next step:
- Add editable macros in SettingsActivity.
- Store custom macros in SharedPreferences.
- Render macro buttons dynamically.

Files to extend:
- `MacroRepository.kt`
- `KeyboardPrefs.kt`
- `SettingsActivity.kt`
- `TermuxKeyboardService.kt`

### 2. Clipboard Manager
Add clipboard history row:
- Recent copied text
- Paste buttons
- Clear clipboard history

New file:
- `ClipboardHistory.kt`

### 3. True Key Preview Popup
Current v5 has a Toast-based preview hook.
Next step:
- Replace Toast with a floating PopupWindow above pressed key.

Files to extend:
- `TermuxKeyboardService.kt`

### 4. Smart Prediction Engine
Current v5 has local command suggestions.
Next step:
- Add command frequency ranking.
- Save used commands.
- Suggest based on partial input.

Files to extend:
- `PredictionEngine.kt`
- `KeyboardPrefs.kt`

### 5. Layout Profiles
Add:
- Compact
- Normal
- Large
- Landscape split layout

Files to extend:
- `KeyboardPrefs.kt`
- `SettingsActivity.kt`
- `TermuxKeyboardService.kt`

## v6 Principle

Do not turn this into a normal keyboard clone. Keep it terminal-first:
- fast command input
- strong Termux controls
- programmable macro system
- clean hacker UI
