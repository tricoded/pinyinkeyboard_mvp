package com.example.pinyinkeyboard

import android.inputmethodservice.InputMethodService
import android.inputmethodservice.Keyboard
import android.inputmethodservice.KeyboardView
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener
import android.view.View
import android.view.KeyEvent

class PinyinKeyboardService : InputMethodService(), OnKeyboardActionListener {
    override fun onEvaluateInputViewShown(): Boolean {
        super.onEvaluateInputViewShown()
        return true
    }

    override fun onCreateInputView(): View {
        val keyboardView =
            layoutInflater.inflate(R.layout.keyboard_layout, null) as KeyboardView
        val keyboard = Keyboard(this, R.xml.custom_keyboard)
        keyboardView.setKeyboard(keyboard)
        keyboardView.setOnKeyboardActionListener(this)
        return keyboardView
    }

    override fun onPress(i: Int) {}

    override fun onRelease(i: Int) {}

    override fun onKey(i: Int, ints: IntArray) {
        val inputConnection = getCurrentInputConnection() ?: return


        val pinyinTones = mapOf(
            'a' to listOf('ā', 'á', 'ǎ', 'à'),
            'o' to listOf('ō', 'ó', 'ǒ', 'ò'),
            'e' to listOf('ē', 'é', 'ě', 'è'),
            'i' to listOf('ī', 'í', 'ǐ', 'ì'),
            'u' to listOf('ū', 'ú', 'ǔ', 'ù'),
            'ü' to listOf('ǖ', 'ǘ', 'ǚ', 'ǜ')
        )


        val toneIndex = when (i) {
            -22 -> 0
            -23 -> 1
            -24 -> 2
            -25 -> 3
            -26 -> {
                inputConnection.deleteSurroundingText(1, 0)
                return
            }
            else -> null
        }


        if (toneIndex != null) {
            val lastTextBeforeCursor = inputConnection.getTextBeforeCursor(10, 0) ?: return
            if (lastTextBeforeCursor.isEmpty()) return

            val lastWord = lastTextBeforeCursor.split(" ").last()

            val vowels = listOf('a', 'o', 'e', 'i', 'u', 'ü')
            val consonants = listOf(
                "zh", "ch", "sh", // Double-character consonants
                "b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h",
                "j", "q", "x", "z", "c", "r", "s", "y", "w"
            )

            val consonantRegex = "(${consonants.joinToString("|")})(?=[aeiouü])".toRegex()
            val lastConsonantMatch = consonantRegex.findAll(lastWord).lastOrNull()
            val lastConsonantIndex = lastConsonantMatch?.range?.last ?: -1

            val vowelIndices = lastWord.mapIndexedNotNull { index, c -> if (c in vowels) index else null }
            val validVowelIndices = vowelIndices.filter { it > lastConsonantIndex }

            if (validVowelIndices.isNotEmpty()) {
                val toneTargetIndex = when {
                    validVowelIndices.size == 1 -> validVowelIndices[0]
                    'a' in lastWord.substring(lastConsonantIndex + 1) -> lastWord.indexOf('a', lastConsonantIndex + 1)
                    'o' in lastWord.substring(lastConsonantIndex + 1) -> lastWord.indexOf('o', lastConsonantIndex + 1)
                    'e' in lastWord.substring(lastConsonantIndex + 1) -> lastWord.indexOf('e', lastConsonantIndex + 1)
                    else -> validVowelIndices.last()
                }

                val toneVowel = lastWord[toneTargetIndex]
                if (pinyinTones.containsKey(toneVowel)) {
                    val tonedVowel = pinyinTones[toneVowel]?.get(toneIndex) ?: toneVowel
                    val newWord = lastWord.substring(0, toneTargetIndex) +
                            tonedVowel +
                            lastWord.substring(toneTargetIndex + 1)

                    val textBeforeWord = lastTextBeforeCursor.dropLast(lastWord.length)
                    inputConnection.deleteSurroundingText(lastTextBeforeCursor.length, 0)
                    inputConnection.commitText(textBeforeWord.toString() + newWord.toString(), 1)
                }
            }

        return
        }


        when (i) {
            -1 -> inputConnection.commitText("zh", 1)
            -2 -> inputConnection.commitText("an", 1)
            -3 -> inputConnection.commitText("en", 1)
            -4 -> inputConnection.commitText("in", 1)
            -5 -> inputConnection.commitText("un", 1)
            -6 -> inputConnection.commitText("ün", 1)
            -7 -> inputConnection.commitText("er", 1)
            -8 -> inputConnection.commitText("ong", 1)
            -9 -> inputConnection.commitText("ch", 1)
            -10 -> inputConnection.commitText("ai", 1)
            -11 -> inputConnection.commitText("ao", 1)
            -12 -> inputConnection.commitText("ie", 1)
            -13 -> inputConnection.commitText("ing", 1)
            -14 -> inputConnection.commitText("sh", 1)
            -15 -> inputConnection.commitText("ei", 1)
            -16 -> inputConnection.commitText("ou", 1)
            -17 -> inputConnection.commitText("üe", 1)
            -18 -> inputConnection.commitText("eng", 1)
            -19 -> inputConnection.commitText("ui", 1)
            -20 -> inputConnection.commitText("iu", 1)
            -21 -> inputConnection.commitText("ang", 1)
            else -> inputConnection.commitText(i.toChar().toString(), 1)
        }
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val inputConnection = getCurrentInputConnection() ?: return super.onKeyDown(keyCode, event)

        val keyMap = mapOf(
            KeyEvent.KEYCODE_1 to "b",
            KeyEvent.KEYCODE_2 to "d",
            KeyEvent.KEYCODE_3 to "y",
            KeyEvent.KEYCODE_4 to "w",
            KeyEvent.KEYCODE_5 to "zh",
            KeyEvent.KEYCODE_6 to "an",
            KeyEvent.KEYCODE_7 to "en",
            KeyEvent.KEYCODE_8 to "in",
            KeyEvent.KEYCODE_9 to "un",
            KeyEvent.KEYCODE_0 to "ün",
            KeyEvent.KEYCODE_MINUS to "er",
            KeyEvent.KEYCODE_DEL to "ong",

            // Second row
            KeyEvent.KEYCODE_Q to "p",
            KeyEvent.KEYCODE_W to "t",
            KeyEvent.KEYCODE_E to "g",
            KeyEvent.KEYCODE_R to "j",
            KeyEvent.KEYCODE_T to "ch",
            KeyEvent.KEYCODE_Y to "z",
            KeyEvent.KEYCODE_U to "i",
            KeyEvent.KEYCODE_I to "a",
            KeyEvent.KEYCODE_O to "ai",
            KeyEvent.KEYCODE_P to "ao",
            KeyEvent.KEYCODE_LEFT_BRACKET to "ie",
            KeyEvent.KEYCODE_RIGHT_BRACKET to "ing",

            // Third row
            KeyEvent.KEYCODE_A to "m",
            KeyEvent.KEYCODE_S to "n",
            KeyEvent.KEYCODE_D to "k",
            KeyEvent.KEYCODE_F to "q",
            KeyEvent.KEYCODE_G to "sh",
            KeyEvent.KEYCODE_H to "c",
            KeyEvent.KEYCODE_J to "u",
            KeyEvent.KEYCODE_K to "o",
            KeyEvent.KEYCODE_L to "ei",
            KeyEvent.KEYCODE_SEMICOLON to "ou",
            KeyEvent.KEYCODE_APOSTROPHE to "üe",
            KeyEvent.KEYCODE_ENTER to "eng",

            // Fourth row
            KeyEvent.KEYCODE_Z to "f",
            KeyEvent.KEYCODE_X to "l",
            KeyEvent.KEYCODE_C to "h",
            KeyEvent.KEYCODE_V to "x",
            KeyEvent.KEYCODE_B to "r",
            KeyEvent.KEYCODE_N to "s",
            KeyEvent.KEYCODE_M to "ü",
            KeyEvent.KEYCODE_COMMA to "e",
            KeyEvent.KEYCODE_PERIOD to "ui",
            KeyEvent.KEYCODE_SLASH to "iu",
            KeyEvent.KEYCODE_SHIFT_RIGHT to "ang",

            // UNUSED
// Esc row
            KeyEvent.KEYCODE_F1 to "",
            KeyEvent.KEYCODE_F2 to "",
            KeyEvent.KEYCODE_F3 to "",
            KeyEvent.KEYCODE_F4 to "",
            KeyEvent.KEYCODE_F5 to "",
            KeyEvent.KEYCODE_F6 to "",
            KeyEvent.KEYCODE_F7 to "",
            KeyEvent.KEYCODE_F8 to "",
            KeyEvent.KEYCODE_F9 to "",
            KeyEvent.KEYCODE_F10 to "",
            KeyEvent.KEYCODE_F11 to "",
            KeyEvent.KEYCODE_F12 to "",
            KeyEvent.KEYCODE_ESCAPE to "",


// Number row
            KeyEvent.KEYCODE_EQUALS to "",


// Tab row
            KeyEvent.KEYCODE_TAB to "",
            KeyEvent.KEYCODE_BACKSLASH to "",


// Caps Lock row
            KeyEvent.KEYCODE_CAPS_LOCK to "",


// Shift row
            KeyEvent.KEYCODE_SHIFT_LEFT to "",


// Ctrl row
            KeyEvent.KEYCODE_CTRL_LEFT to "",
            KeyEvent.KEYCODE_ALT_LEFT to "",
            KeyEvent.KEYCODE_DPAD_UP to "",
            KeyEvent.KEYCODE_DPAD_RIGHT to "",
            KeyEvent.KEYCODE_FUNCTION to "",
            KeyEvent.KEYCODE_WINDOW to "",

            )

        val toneMap = mapOf(
            KeyEvent.KEYCODE_ALT_RIGHT to 0, // First tone
            KeyEvent.KEYCODE_CTRL_RIGHT to 1, // Second tone
            KeyEvent.KEYCODE_DPAD_LEFT to 2, // Third tone
            KeyEvent.KEYCODE_DPAD_DOWN to 3  // Fourth tone
        )

        if (keyCode == KeyEvent.KEYCODE_SPACE) {
            inputConnection.deleteSurroundingText(Int.MAX_VALUE, Int.MAX_VALUE)
            return true
        }

        if (keyCode in keyMap.keys) {
            inputConnection.commitText(keyMap[keyCode], 1)
            return true
        }

        if (keyCode in toneMap.keys) {
            val toneIndex = toneMap[keyCode] ?: return true

            val lastTextBeforeCursor = inputConnection.getTextBeforeCursor(10, 0)?.toString() ?: return true
            if (lastTextBeforeCursor.isEmpty()) return true

            val lastWord = lastTextBeforeCursor.split(" ").last()

            val consonants = listOf(
                "zh", "ch", "sh",
                "b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h",
                "j", "q", "x", "z", "r", "c", "s", "y", "w"
            )

            val consonantRegex = "(${consonants.joinToString("|")})(?=[aeiouü])".toRegex()
            val lastSyllableStart = consonantRegex.findAll(lastWord).lastOrNull()?.range?.first ?: 0
            val lastSyllable = lastWord.substring(lastSyllableStart)

            val vowels = listOf('a', 'o', 'e', 'i', 'u', 'ü')
            val vowelIndices = lastSyllable.mapIndexedNotNull { index, c -> if (c in vowels) index else null }

            if (vowelIndices.isNotEmpty()) {
                val toneTargetIndex = when {
                    vowelIndices.size == 1 -> vowelIndices[0]
                    'a' in lastSyllable -> lastSyllable.indexOf('a')
                    'o' in lastSyllable -> lastSyllable.indexOf('o')
                    'e' in lastSyllable -> lastSyllable.indexOf('e')
                    else -> vowelIndices.last()
                }

                val toneVowel = lastSyllable[toneTargetIndex]
                val tonedVowel = applyTone(toneVowel, toneIndex)

                tonedVowel?.let {
                    val newSyllable = lastSyllable.substring(0, toneTargetIndex) + it + lastSyllable.substring(toneTargetIndex + 1)
                    val updatedWord = lastWord.substring(0, lastSyllableStart) + newSyllable

                    val textBeforeWord = lastTextBeforeCursor.dropLast(lastWord.length)
                    inputConnection.deleteSurroundingText(lastTextBeforeCursor.length, 0)
                    inputConnection.commitText(textBeforeWord + updatedWord, 1)
                }
            }

            return true
        }

        return super.onKeyDown(keyCode, event)

    }

    private fun applyTone(vowel: Char, toneIndex: Int): Char? {
        val pinyinTones = mapOf(
            'a' to listOf('ā', 'á', 'ǎ', 'à'),
            'o' to listOf('ō', 'ó', 'ǒ', 'ò'),
            'e' to listOf('ē', 'é', 'ě', 'è'),
            'i' to listOf('ī', 'í', 'ǐ', 'ì'),
            'u' to listOf('ū', 'ú', 'ǔ', 'ù'),
            'ü' to listOf('ǖ', 'ǘ', 'ǚ', 'ǜ')
        )

        return pinyinTones[vowel]?.getOrNull(toneIndex)
    }


    override fun onText(charSequence: CharSequence) {}
    override fun swipeLeft() {}
    override fun swipeRight() {}
    override fun swipeDown() {}
    override fun swipeUp() {}
}