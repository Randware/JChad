package net.jchad.server.model.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *  Can be used to run a separate {@link Thread} checking for file changes in a specific directory.
 */
public class ConfigWatcher extends Thread {
    /**
     * Path which will be watched.
     */
    private final Path path;

    /**
     * Code that gets called when an event was registered.
     * Also returns the {@link WatchEvent} that got registered.
     */
    private final Consumer<WatchEvent<?>> callback;

    /**
     * Defines if callback code should be executed when an event was registered
     */
    private boolean running = true;

    /**
     *
     * @param path {@link Path} which will be watched
     * @param callback Code that gets called when an event was registered.
     *                 Also returns the {@link WatchEvent} that got registered.
     * @throws IOException If the thread runs into a file system error
     */
    public ConfigWatcher(Path path, Consumer<WatchEvent<?>> callback) throws IOException {
        this.path = path;
        this.callback = callback;
    }

    /**
     * Returns <code>true</code> if server is running, <code>false</code> if not.
     *
     * @return <code>true</code> if server is running, <code>false</code> if not
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Pause the watching process.
     */
    public void pauseWatching() {
        running = false;
    }

    /**
     * Continue the watching process.
     */
    public void continueWatching() {
        running = true;
    }

    /**
     * Run the actual loop checking for file modifications in the specified path.
     *
     * @throws UncheckedIOException If the thread runs into a file system error
     */
    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            while (true) {
                if(!isRunning()) continue;

                WatchKey key;
                try {
                    key = watcher.poll(25, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    return;
                }

                if (key == null) continue;

                WatchEvent<?> event = key.pollEvents().getFirst();
                WatchEvent.Kind<?> kind = event.kind();

                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    callback.accept(event);
                }

                key.reset();

            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
