package p2;

import java.util.Random;

public class Producer implements Runnable {
    private Drop drop;
    private int[] mas;
    private int N;

    public Producer(Drop drop, int n) {
        this.drop = drop;
        this.N=n;
        mas = new int[N];
        for (int i = 0; i<N; i++){
            mas[i] = i;
        }
    }

    public void run() {
        Random random = new Random();

        for (int i = 0; i < mas.length; i++) {
            drop.put(mas[i]);
            try {
                Thread.sleep(random.nextInt(5));
            } catch (InterruptedException e) {}
        }
        drop.put(-1);
    }
}