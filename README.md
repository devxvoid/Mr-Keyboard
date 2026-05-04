# MrRobot Keyboard v5

Production-level Android input method keyboard for Termux users.

## Package

`com.robot.mrkeyboard`

## Version

`5.0`

## Features

- Full QWERTY keyboard
- Number row
- Symbol layer with `SYM / ABC` toggle
- ESC, TAB, CTRL, ALT
- Arrow controls
- SHIFT, DEL, ENTER, SPACE
- HOME, END, PAGE UP, PAGE DOWN, INSERT, FORWARD DELETE
- CTRL+C and CTRL+D shortcuts
- Termux command shortcuts:
  - `ls`
  - `cd`
  - `pwd`
  - `clear`
  - `pkg update && pkg upgrade`
- Macro row:
  - update
  - storage
  - files
  - python
  - server
  - git
- Three built-in themes:
  - Green Terminal
  - Red Alert
  - Blue Matrix
- Vibration feedback
- Settings activity
- Compact mode
- Command row toggle
- Macro row toggle
- Key preview hook
- Prediction hook

## Build

Use Java 17 and Gradle 8.7.

```bash
gradle clean assembleDebug
```

## Enable

Install APK, open the app, then:

1. Open keyboard settings
2. Enable MrRobot Keyboard
3. Switch current keyboard
