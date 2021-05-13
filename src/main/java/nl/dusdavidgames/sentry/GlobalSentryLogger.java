package nl.dusdavidgames.sentry;

import io.sentry.protocol.SentryId;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * This class is for capturing exceptions thrown by our plugin but logged in the server logger
 * As an example:
 *   A exception not captured by the plugin in the listener
 * That exception will go to the global server logger
 */
public class GlobalSentryLogger extends Handler {

    @Getter
    private static GlobalSentryLogger instance;

    static void initialize() {
        if (instance == null) {
            instance = new GlobalSentryLogger();
        } else {
            throw new IllegalStateException("Global sentry logger already initialized");
        }

        Bukkit.getServer().getLogger().addHandler(instance);
    }

    @Override
    public void publish(LogRecord record) {
        if (record.getThrown() == null) return;
        if (!record.getMessage().startsWith("Could not pass event")) return;


        // Split message to find plugin name
        String[] splitMessage = record.getMessage().split(" to ");
        if (splitMessage.length <= 1) return;


        // Message contains "fullName" so we get the last split from the array and split the fullName
        // Example of fullName "sentry-logger v1.0.0"
        // We split it on a space so we can get the name "sentry-logger"
        String pluginName = splitMessage[splitMessage.length - 1].split(" ")[0];

        // Send thrown exception to the hub belonging to the
        SentryLoggingHandler handler = BukkitSentryLogger.getHandler(pluginName);

        // Check if plugin has a logger attached to it
        if (handler == null) return;
        handler.getHub().captureException(record.getThrown());
    }

    @Override
    public void flush() {
        // Global logger is not responsible for flushing any of the hubs
    }

    @Override
    public void close() throws SecurityException {
        // Global logger is not responsible for closing any of the hubs
    }
}
