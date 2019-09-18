package com.vietlh.wethoong.networking;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageContainer {
    public static final String MESSAGE = "message";
    public static final String ERROR = "error";
    public static final String DATA = "data";

    private HashMap<String, Object> messages = new HashMap<>();

    public void setValue(String key, Object value) {
        messages.put(key, value);
    }

    public void appendValue(String key, Object value) {
        if (messages.get(key) instanceof String) {
            String newValue = messages.get(key) + "\n" + value;
            messages.put(key, newValue);
            return;
        }
        if (messages.get(key) instanceof ArrayList) {
            ArrayList<Object> newValue = (ArrayList<Object>) messages.get(key);
            newValue.add(value);
            messages.put(key, value);
            return;
        }

        setValue(key, value);
    }

    public Object getValue(String key) {
        return messages.get(key);
    }
}
