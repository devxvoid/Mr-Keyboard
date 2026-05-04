package com.robot.mrkeyboard

class PredictionEngine {
    fun suggestions(prefix: String): List<String> {
        if (prefix.isBlank()) return emptyList()

        val commands = listOf(
            "pkg update",
            "pkg upgrade",
            "pkg install",
            "clear",
            "cd /storage/emulated/0/Download",
            "ls -la",
            "pwd",
            "python",
            "git status",
            "git pull",
            "termux-setup-storage"
        )

        return commands
            .filter { it.startsWith(prefix, ignoreCase = true) }
            .take(3)
    }
}
