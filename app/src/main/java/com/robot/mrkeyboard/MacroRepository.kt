package com.robot.mrkeyboard

class MacroRepository {
    data class Macro(val label: String, val command: String)

    fun defaultMacros(): List<Macro> {
        return listOf(
            Macro("update", "pkg update && pkg upgrade"),
            Macro("storage", "termux-setup-storage"),
            Macro("files", "cd /storage/emulated/0/Download"),
            Macro("python", "python "),
            Macro("server", "python -m http.server 8080"),
            Macro("git", "git status")
        )
    }
}
