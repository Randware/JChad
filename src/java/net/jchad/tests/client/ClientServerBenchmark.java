package net.jchad.tests.client;

import net.jchad.client.model.client.ViewCallback;
import net.jchad.client.model.store.chat.ClientChatMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientServerBenchmark {

    private static final int connections = 500;
    private static final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

    public static void main(String[] args) {
        for (int i = 1; i<connections ;i++) {
            service.submit(new TestConnection());
        }
        while (true) {

        }
    }

}
