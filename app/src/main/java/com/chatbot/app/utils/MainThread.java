package com.chatbot.app.utils;

import android.os.Handler;
import android.os.Looper;

public final class MainThread {

    private static final Handler HANDLER = new Handler(Looper.getMainLooper());

    private MainThread() {
    }

    public static void run(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            HANDLER.post(runnable);
        }
    }
}
