#!/usr/bin/env bash
set -e

# Example: create/overwrite OpenBoard layout & theme files
# Adjust paths and content to your project structure.

# Ensure directories exist
mkdir -p app/src/main/res/xml
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/values

# 1) iOS-style QWERTY layout
cat > app/src/main/res/xml/qwerty_ios.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<Keyboard xmlns:android="http://schemas.android.com/apk/res/android"
    android:keyWidth="10%p"
    android:horizontalGap="0px"
    android:verticalGap="0px"
    android:keyHeight="@dimen/key_height">

    <!-- Row 1 -->
    <Row>
        <Key android:codes="113" android:keyLabel="q" />
        <Key android:codes="119" android:keyLabel="w" />
        <Key android:codes="101" android:keyLabel="e" />
        <Key android:codes="114" android:keyLabel="r" />
        <Key android:codes="116" android:keyLabel="t" />
        <Key android:codes="121" android:keyLabel="y" />
        <Key android:codes="117" android:keyLabel="u" />
        <Key android:codes="105" android:keyLabel="i" />
        <Key android:codes="111" android:keyLabel="o" />
        <Key android:codes="112" android:keyLabel="p" />
    </Row>

    <!-- Row 2 -->
    <Row android:keyWidth="10%p">
        <Key android:codes="97" android:keyLabel="a" />
        <Key android:codes="115" android:keyLabel="s" />
        <Key android:codes="100" android:keyLabel="d" />
        <Key android:codes="102" android:keyLabel="f" />
        <Key android:codes="103" android:keyLabel="g" />
        <Key android:codes="104" android:keyLabel="h" />
        <Key android:codes="106" android:keyLabel="j" />
        <Key android:codes="107" android:keyLabel="k" />
        <Key android:codes="108" android:keyLabel="l" />
    </Row>

    <!-- Row 3 -->
    <Row>
        <Key
            android:codes="-1"
            android:keyEdgeFlags="left"
            android:keyWidth="14%p"
            android:keyLabel="⇧" />
        <Key android:codes="122" android:keyLabel="z" />
        <Key android:codes="120" android:keyLabel="x" />
        <Key android:codes="99" android:keyLabel="c" />
        <Key android:codes="118" android:keyLabel="v" />
        <Key android:codes="98" android:keyLabel="b" />
        <Key android:codes="110" android:keyLabel="n" />
        <Key android:codes="109" android:keyLabel="m" />
        <Key
            android:codes="-5"
            android:keyEdgeFlags="right"
            android:keyWidth="14%p"
            android:keyLabel="⌫" />
    </Row>

    <!-- Row 4 -->
    <Row>
        <Key
            android:codes="-2"
            android:keyWidth="14%p"
            android:keyLabel="123" />
        <Key
            android:codes="@integer/code_switch_to_emoji"
            android:keyWidth="14%p"
            android:keyLabel="😊" />
        <Key
            android:codes="32"
            android:keyWidth="44%p"
            android:keyLabel="space" />
        <Key
            android:codes="10"
            android:keyWidth="28%p"
            android:keyLabel="return" />
    </Row>

</Keyboard>
EOF

# 2) iOS-style key backgrounds
cat > app/src/main/res/drawable/key_ios_normal.xml << 'EOF'
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <shape>
            <solid android:color="#E5E5EA" />
            <corners android:radius="12dp" />
        </shape>
    </item>
    <item>
        <shape>
            <solid android:color="#F2F2F7" />
            <corners android:radius="12dp" />
        </shape>
    </item>
</selector>
EOF

cat > app/src/main/res/drawable/key_ios_function.xml << 'EOF'
<selector xmlns:android="http://schemas.android.com/apk/res/android">
    <item android:state_pressed="true">
        <shape>
            <solid android:color="#C7C7CC" />
            <corners android:radius="12dp" />
        </shape>
    </item>
    <item>
        <shape>
            <solid android:color="#D1D1D6" />
            <corners android:radius="12dp" />
        </shape>
    </item>
</selector>
EOF

# 3) iOS-style colors and style stub (you can merge with existing files later)
cat > app/src/main/res/values/ios_keyboard.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ios_keyboard_bg_light">#D1D1D6</color>
    <color name="ios_key_text">#000000</color>
    <color name="ios_key_text_function">#000000</color>

    <style name="KeyboardViewStyle.IOS">
        <item name="keyBackground">@drawable/key_ios_normal</item>
        <item name="keyBackgroundFunction">@drawable/key_ios_function</item>
        <item name="keyTextColor">@color/ios_key_text</item>
        <item name="keyTextColorFunction">@color/ios_key_text_function</item>
        <item name="keyboardBackground">@color/ios_keyboard_bg_light</item>
    </style>
</resources>
EOF

echo "iOS-style OpenBoard files generated."