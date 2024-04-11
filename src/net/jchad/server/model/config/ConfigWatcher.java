package net.jchad.server.model.config;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

public class ConfigWatcher extends Thread {
    private final Path path;
    private boolean stop = false;


    private final Runnable callback;

    public ConfigWatcher(Path path, Runnable callback) throws IOException {
        this.path = path;
        this.callback = callback;

        start();
    }

    public boolean isStopped() {
        return stop;
    }

    public void stopThread() {
        stop = true;
    }

    @Override
    public void run() {
        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

            while (!isStopped()) {
                WatchKey key;
                try {
                    key = watcher.poll(25, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    return;
                }

                if(key == null) continue;

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {

                        callback.run();
                    }
                }
                key.reset();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
