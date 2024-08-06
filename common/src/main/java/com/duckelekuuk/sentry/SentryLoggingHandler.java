package com.duckelekuuk.sentry;

import io.sentry.Hub;
import lombok.Getter;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

class SentryLoggingHandler extends Handler {

    @Getter
    private final Hub hub;

    SentryLoggingHandler(Hub hub) {
        this.hub = hub;
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getThrown() != null) {
            hub.captureException(record.getThrown());
        }
    }

    @Override
    public void flush() {
        hub.flush(1000);
    }

    @Override
    public void close() throws SecurityException {
        hub.close();
    }

}
