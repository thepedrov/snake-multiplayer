package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings;

import org.academiadecodigo.simplegraphics.keyboard.Keyboard;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardEvent;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardEventType;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardHandler;

import java.util.ArrayList;
import java.util.List;

public class KeyboardSettings {

    private static Keyboard keyboard;
    private static List<KeyboardEvent> keyboardEventList = new ArrayList();

    public static void startKeyboard(KeyboardHandler keyboardHandler) {
        keyboard = new Keyboard(keyboardHandler);

        KeyboardEvent keyboardEvent;

        keyboardEvent = new KeyboardEvent();
        keyboardEvent.setKey(KeyboardEvent.KEY_LEFT);
        keyboardEvent.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(keyboardEvent);
        keyboardEventList.add(keyboardEvent);

        keyboardEvent = new KeyboardEvent();
        keyboardEvent.setKey(KeyboardEvent.KEY_UP);
        keyboardEvent.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(keyboardEvent);
        keyboardEventList.add(keyboardEvent);

        keyboardEvent = new KeyboardEvent();
        keyboardEvent.setKey(KeyboardEvent.KEY_RIGHT);
        keyboardEvent.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(keyboardEvent);
        keyboardEventList.add(keyboardEvent);

        keyboardEvent = new KeyboardEvent();
        keyboardEvent.setKey(KeyboardEvent.KEY_DOWN);
        keyboardEvent.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(keyboardEvent);
        keyboardEventList.add(keyboardEvent);

        keyboardEvent = new KeyboardEvent();
        keyboardEvent.setKey(KeyboardEvent.KEY_SPACE);
        keyboardEvent.setKeyboardEventType(KeyboardEventType.KEY_PRESSED);
        keyboard.addEventListener(keyboardEvent);
        keyboardEventList.add(keyboardEvent);
    }

    public static void stopKeyboard() {
        for (KeyboardEvent event : keyboardEventList) {
            keyboard.removeEventListener(event);
        }

        keyboardEventList.clear();
        keyboard = null;
    }

}