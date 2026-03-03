package com.ui.lib;
// Source
// https://www.javaspring.net/blog/java-correct-pattern-for-implementing-listeners/

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Broadcaster<T> {
    private final List<T> listeners = new CopyOnWriteArrayList<>();

    public void addListener(T listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(T listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }
 
    // Notify all listeners using a consumer (flexible for different listener methods)
    protected void notifyListeners(java.util.function.Consumer<T> notifier) {
        for (T listener : listeners) {
            notifier.accept(listener); // Call the listener's callback method
        }
    }   
}
