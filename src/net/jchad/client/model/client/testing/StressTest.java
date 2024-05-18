package net.jchad.client.model.client.testing;

public class StressTest {

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 10_000; i++) {
            Thread.sleep(500);
            Thread thread = Thread.startVirtualThread(new ClientSampleCodeRunnable());
            thread.setName("Thread num: " + i);
            System.out.println("(" + thread.getName() + ") just started");
        }
        while (true) {

        }
    }
}
