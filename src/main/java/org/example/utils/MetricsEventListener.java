package org.example.utils;

import okhttp3.Call;
import okhttp3.EventListener;
import okhttp3.logging.LoggingEventListener;

public class MetricsEventListener extends EventListener {
     static final Factory FACTORY = new Factory() {
        @Override public EventListener create(Call call) {
            if (Math.random() < 0.10) {
                return new LoggingEventListener.Factory().create(call);
            }
            else {
                return EventListener.NONE;
            }
        }
    };
}
