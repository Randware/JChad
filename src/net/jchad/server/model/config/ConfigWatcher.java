package net.jchad.server.model.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class ConfigWatcher extends Thread {
    private final Path path;
    private boolean running = true;

    private final Runnable callback;

    public ConfigWatcher(Path path, Runnable callback) throws IOException {
        this.path = path;
        this.callback = callback;
    }

    public boolean isRunning() {
        return running;
    }

    public void pauseWatching() {
        running = false;
    }

    public void continueWatching() {
        running = true;
    }

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
                    System.out.println(event.context());

                    callback.run();
                }

                key.reset();

            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
