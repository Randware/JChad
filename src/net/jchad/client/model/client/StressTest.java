package net.jchad.client.model.client;

public class StressTest {

    public static void main(String[] args) {
        for (int i = 0; i < 30_000; i++) {
            Thread thread = Thread.startVirtualThread(new ClientSampleCodeRunnable());
            thread.setName("Thread num: " + i);
            System.out.println("(" + thread.getName() + ") just started");
        }
        while (true) {

        }
    }
}
