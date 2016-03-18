package com.example.eiditortext;

import android.text.method.DialerKeyListener;

public class ContactNumberListener extends DialerKeyListener {
    private static ContactNumberListener keyListener;

    private ContactNumberListener() {
        
    }
    
    /**
     * The characters that are used.
     * 
     * @see KeyEvent#getMatch
     * @see #getAcceptedChars
     */
    public static final char[] CHARACTERS = new char[] {
            'w', 'p', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '*', '#', ',', ';', 'N'
    };

    @Override
    protected char[] getAcceptedChars() {
        return CHARACTERS;
    }

    public static ContactNumberListener getInstance() {
        if (keyListener == null) {
            keyListener = new ContactNumberListener();
        }
        return keyListener;
    }
}
