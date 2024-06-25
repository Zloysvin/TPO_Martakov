package p2;

import java.util.Random;

public class Consumer implements Runnable {
    private Drop drop;
    private int N;

    public Consumer(Drop drop, int n) {
        this.drop = drop;
        this.N=n;
    }

    public void run() {
        Random random = new Random();
        for (int message = drop.take(); message!=-1; message = drop.take()) {
            System.out.format("MESSAGE RECEIVED: %d%n", message);
            try {
                Thread.sleep(random.nextInt(5));
            } catch (InterruptedException e) {}
        }
    }
}
