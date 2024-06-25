package p2;

public class ProducerConsumerExample {
    public static void main(String[] args) {
        int N=500; //1000, 10000
        Drop drop = new Drop();
        (new Thread(new Producer(drop, N))).start();
        (new Thread(new Consumer(drop, N))).start();
    }
}
