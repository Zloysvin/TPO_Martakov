package p3;
public class main {
    public static void main(String[] args)  throws InterruptedException {
        jornal j = new jornal();
        Thread[] threads = new Thread[4]; // Масив для зберігання посилань на створені потоки
        Runnable r = () -> {
            threads[0] = new Thread(new producer(j, "Lecturer 1", true, new int[]{0,1,2}));
            threads[1] = new Thread(new producer(j, "Assistant 1", false, new int[]{0}));
            threads[2] = new Thread(new producer(j, "Assistant 2", false, new int[]{1}));
            threads[3] = new Thread(new producer(j, "Assistant 3", false, new int[]{2}));

            for (Thread t : threads) {
                t.start(); // Запускаємо кожен потік
            }
        };

        Thread t = new Thread(r);
        t.start();
        t.join(); // Очікуємо завершення основного потоку

        try {
            for (Thread thread : threads) {
                thread.join(); // Очікуємо завершення кожного потоку
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        j.print();
    }
}
