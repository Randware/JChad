package net.jchad.shared.files;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Can be used to run a separate {@link Thread} checking for file system changes in a specific directory.
 */
public class PathWatcher extends Thread {
    /**
     * The {@link Path}s that will be watched.
     */
    private ArrayList<Path> paths;

    private WatchService watcher;

    /**
     * Code that gets called when an event was registered.
     * Also returns the {@link WatchEvent} that got registered.
     */
    private final BiConsumer<Path, WatchEvent.Kind<?>> callback;

    /**
     * Code that gets called when an error occured.
     * Also returns the {@link Exception} that occured.
     */
    private final Consumer<Exception> errorCallback;

    /**
     * Defines if callback code should be executed when an event was registered.
     */
    private boolean running = true;

    /**
     * Set true to stop the PathWatcher.
     */
    private boolean exit = false;

    /**
     * Stores a timestamp of when this PathWatcher was started
     */
    private long startTimestamp;

    /**
     * Stores recently created files with a timestamp. This is used,
     * so recently created files aren't detected as modified until after a specified time.
     * This is needed, because when a new file is created with content, this still gets
     * detected as a modified file, because it gets created first, then modified.
     * This is technically correct behaviour, but not desired in this use case.
     */
    private final Map<Path, Long> recentlyCreatedFiles = new HashMap<>();

    /**
     * @param path          {@link Path} which will be watched
     * @param callback      Code that gets called when an event was registered.
     *                      Also returns the {@link WatchEvent} that got registered.
     * @param errorCallback The method which will be called when the PathWatcher
     *                      runs into an error.
     */
    public PathWatcher(Path path, BiConsumer<Path, WatchEvent.Kind<?>> callback, Consumer<Exception> errorCallback) throws IOException {
        this(Collections.singleton(path), callback, errorCallback);
    }

    /**
     * @param paths         Any {@link Collection} of {@link Path} objects, which will all be watched.
     * @param callback      Code that gets called when an event was registered.
     *                      Also returns the {@link WatchEvent} that got registered.
     * @param errorCallback The method which will be called when the PathWatcher
     *                      runs into an error.
     */
    public PathWatcher(Collection<Path> paths, BiConsumer<Path, WatchEvent.Kind<?>> callback, Consumer<Exception> errorCallback) throws IOException {
        this.paths = new ArrayList<>(paths);
        this.callback = callback;
        this.errorCallback = errorCallback;

        watcher = FileSystems.getDefault().newWatchService();

        for (Path path : this.paths) {
            registerPath(path);
        }
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
     * Stops the PathWatcher thread gracefully.
     */
    public void stopWatcher() {
        exit = true;
        interrupt();
    }

    /**
     * Add a path for the PathWatcher to watch.
     * Does nothing if the path is already added.
     *
     * @param path the {@link Path} that should be added
     * @throws IOException if the given path couldn't be registered to the watcher
     */
    public void addPath(Path path) throws IOException {
        if (!paths.contains(path)) {
            registerPath(path);
            paths.add(path);
        }
    }

    private void registerPath(Path path) throws IOException {
        path.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_CREATE);
    }

    /**
     * Run the actual loop checking for file system modifications and creations in the specified path.
     */
    @Override
    public void run() {
        startTimestamp = System.currentTimeMillis();

        while (!exit) {
            try {
                while (true) {
                    if (!isRunning()) continue;

                    final WatchKey key = watcher.take();

                    /*
                     * Let thread sleep for 100ms to prevent the same file edit from being recognized twice.
                     * This happens, because a file gets edited once for changing metadata and a second time
                     * for actually updating the file content.
                     */
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ignore) {}

                    for (final WatchEvent<?> event : key.pollEvents()) {
                        Path fullPath = ((Path) key.watchable()).resolve((Path) event.context()).toAbsolutePath().normalize();

                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                            // Add newly created file to map with timestamp
                            if(!Files.isDirectory(fullPath)) {
                                recentlyCreatedFiles.put(fullPath, System.currentTimeMillis());
                            }

                            callback.accept(fullPath, event.kind());
                        } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            if (!recentlyCreatedFiles.containsKey(fullPath) ||
                                    System.currentTimeMillis() - recentlyCreatedFiles.get(fullPath) > TimeUnit.MILLISECONDS.toMillis(100)) {
                                recentlyCreatedFiles.remove(fullPath);
                                callback.accept(fullPath, event.kind());
                            }
                        } else {
                            callback.accept(fullPath, event.kind());
                        }
                    }

                    key.reset();
                }
            } catch (InterruptedException e) {
                errorCallback.accept(e);
            }
        }
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }
}
