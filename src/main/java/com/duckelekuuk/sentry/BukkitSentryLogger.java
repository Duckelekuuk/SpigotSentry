package com.duckelekuuk.sentry;

import io.sentry.Hub;
import io.sentry.Sentry;
import io.sentry.SentryOptions;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Consumer;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class BukkitSentryLogger {

    private static final Map<String, SentryLoggingHandler> LOGGING_PLUGINS = new HashMap<>();


    public static void injectPlugin(JavaPlugin plugin, Consumer<SentryOptions> sentryOptions) {
        SentryOptions localSentryOptions = new SentryOptions();

        sentryOptions.accept(localSentryOptions);
        injectPlugin(plugin, localSentryOptions);
    }

    public static void injectPlugin(JavaPlugin plugin, SentryOptions sentryOptions) {
        if (LOGGING_PLUGINS.containsKey(plugin.getName())) {
            throw new IllegalArgumentException("The plugin is already injected");
        }

        if (LOGGING_PLUGINS.isEmpty()) {
            injectGlobalLogger();
        }

        Hub hub = new Hub(sentryOptions);

        SentryLoggingHandler sentryLoggingHandler = new SentryLoggingHandler(hub);
        LOGGING_PLUGINS.put(plugin.getName(), sentryLoggingHandler);

        plugin.getLogger().addHandler(sentryLoggingHandler);

    }

    public static void unInject(JavaPlugin javaPlugin) {
        SentryLoggingHandler remove = LOGGING_PLUGINS.remove(javaPlugin.getName());

        if (remove != null) remove.getHub().close();
    }

    private static void injectGlobalLogger() {
        GlobalSentryLogger.initialize();
    }

    public static SentryLoggingHandler getHandler(String plugin) {
        return LOGGING_PLUGINS.get(plugin);
    }
}
