package com.duckelekuuk.sentry;

import io.sentry.Hub;
import io.sentry.SentryOptions;
import lombok.experimental.UtilityClass;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@UtilityClass
public class SpigotSentryLogger {

    private static final Map<String, SentryLoggingHandler> INJECTED_PLUGINS = new HashMap<>();

    public static void injectPlugin(JavaPlugin plugin, Consumer<SentryOptions> sentryOptions) {
        SentryOptions localSentryOptions = new SentryOptions();

        sentryOptions.accept(localSentryOptions);
        injectPlugin(plugin, localSentryOptions);
    }

    public static void injectPlugin(JavaPlugin plugin, SentryOptions sentryOptions) {
        if (INJECTED_PLUGINS.containsKey(plugin.getName())) {
            throw new IllegalArgumentException("The plugin is already injected");
        }

        if (INJECTED_PLUGINS.isEmpty()) {
            injectGlobalLogger();
        }

        Hub hub = new Hub(sentryOptions);

        SentryLoggingHandler sentryLoggingHandler = new SentryLoggingHandler(hub);
        INJECTED_PLUGINS.put(plugin.getName(), sentryLoggingHandler);

        plugin.getLogger().addHandler(sentryLoggingHandler);

    }

    public static void unInject(JavaPlugin javaPlugin) {
        SentryLoggingHandler remove = INJECTED_PLUGINS.remove(javaPlugin.getName());

        if (remove != null) remove.getHub().close();
    }

    private static void injectGlobalLogger() {
        GlobalSpigotSentryLogger.initialize();
    }

    public static SentryLoggingHandler getHandler(String plugin) {
        return INJECTED_PLUGINS.get(plugin);
    }
}
