package com.duckelekuuk.sentry;

import io.sentry.Hub;
import io.sentry.SentryOptions;
import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

@UtilityClass
public class SimpleSentryLogger {

    private static final Map<Logger, SentryLoggingHandler> INJECTED_LOGGERS = new HashMap<>();

    public static void inject(Logger logger, Consumer<SentryOptions> sentryOptions) {
        SentryOptions localSentryOptions = new SentryOptions();

        sentryOptions.accept(localSentryOptions);
        inject(logger, localSentryOptions);
    }

    public static void inject(Logger logger, SentryOptions sentryOptions) {
        if (INJECTED_LOGGERS.containsKey(logger)) {
            throw new IllegalArgumentException("The plugin is already injected");
        }

        Hub hub = new Hub(sentryOptions);

        SentryLoggingHandler sentryLoggingHandler = new SentryLoggingHandler(hub);
        INJECTED_LOGGERS.put(logger, sentryLoggingHandler);

        logger.addHandler(sentryLoggingHandler);
    }

    public static void unInject(Logger logger) {
        SentryLoggingHandler remove = INJECTED_LOGGERS.remove(logger);

        if (remove != null) remove.getHub().close();
    }
}
