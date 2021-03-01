package com.duckelekuuk.sentry;

import io.sentry.Hub;
import io.sentry.SentryLevel;
import lombok.Getter;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class SentryLoggingHandler extends Handler {

    @Getter
    private final Hub hub;

    SentryLoggingHandler(Hub hub) {
        this.hub = hub;
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getLevel().intValue() < convertLevel(hub.getOptions().getDiagnosticLevel()).intValue()) return;
        
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

    public static Level convertLevel(SentryLevel sentryLevel) {
        switch (sentryLevel) {
            case INFO:
            case DEBUG:
                return Level.INFO;
            case WARNING:
                return Level.WARNING;
            case ERROR:
            case FATAL:
                return Level.SEVERE;
        }
        return Level.INFO;
    }
}
