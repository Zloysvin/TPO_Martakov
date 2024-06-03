package p6;
import java.util.concurrent.locks.ReentrantLock;
class Counter {
    private int count=0;
    public void increment(){
        count++;
    }
    public void decrement(){
        count--;
    }
    public int value (){
        return count;
    }
}
class SyncCounter1{
    private int count=0;
    public synchronized void s_increment(){
        count++;
    }
    public synchronized void s_decrement(){
        count--;
    }
    public synchronized int s_value (){
        return count;
    }
}
class SyncCounter2{
    private int count=0;
    private Object obj = new Object();
    public  void s_increment(){
        synchronized (obj){
            count++;
        }
    }
    public void s_decrement(){
        synchronized (obj){
            count--;
        }
    }
    public int s_value (){
        synchronized (obj){
            return count;
        }
    }
}
public class main {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        Thread t1 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                counter.increment();
            }
        });
        Thread t2 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                counter.decrement();
            }
        });
        t1.start();
        t2.start();
        try{
            t1.join();
            t2.join();
        }
        catch  (InterruptedException e) {
        }
        System.out.print("Async Count = " + counter.value());

        SyncCounter1 counter1 = new SyncCounter1();
        Thread t1_1 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                counter1.s_increment();
            }
        });
        Thread t2_1 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                counter1.s_decrement();
            }
        });
        t1_1.start();
        t2_1.start();
        try{
            t1_1.join();
            t2_1.join();
        }
        catch  (InterruptedException e) {
        }
        System.out.print("\nSync method Count = " + counter1.s_value());

        SyncCounter2 counter2 = new SyncCounter2();
        Thread t1_2 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                counter2.s_increment();
            }
        });
        Thread t2_2 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                counter2.s_decrement();
            }
        });
        t1_2.start();
        t2_2.start();
        try{
            t1_2.join();
            t2_2.join();
        }
        catch  (InterruptedException e) {
        }
        System.out.print("\nSync block Count = " + counter2.s_value());

        ReentrantLock locker = new ReentrantLock();
        Counter counter3 = new Counter();

        Thread t1_3 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                locker.lock();
                try{
                    counter3.increment();
                }
                finally{
                    locker.unlock();
                }
            }
        });
        Thread t2_3 = new Thread(()->{
            for (int i =0; i< 10000; i++){
                locker.lock();
                try{
                    counter3.decrement();
                }
                finally{
                    locker.unlock();
                }
            }
        });
        t1_3.start();
        t2_3.start();
        try{
            t1_3.join();
            t2_3.join();
        }
        catch  (InterruptedException e) {
        }
        System.out.print("\nSync ReentrantLock Count = " + counter2.s_value());


    }
}
