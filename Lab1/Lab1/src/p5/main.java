package p5;
import java.util.logging.Level;
import java.util.logging.Logger;

class myThread extends Thread{
    char c;
    public static int l = 0;
    myThread(String name, char c){
        super(name);
        this.c=c;
    }
    public void run(){
        for (int i=0; i<100; i++){
            for(int j=0; j<100; j++){
                System.out.print(c);
            }
            System.out.print('\n');
        }
    }
}

class Sync{
    private boolean permission;
    private int num;
    private boolean stop;
    public Sync(){
        permission = true;
        num = 0;
        stop = false;
    }
    public synchronized boolean getPermission(){
        return permission;
    }
    public synchronized boolean isStop(){
        return stop;
    }
    public synchronized void waitAndChange(boolean control, char s){
        while(getPermission()!=control){
            try{
                wait();
            } catch (InterruptedException ex){
                Logger.getLogger(Sync.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.print(s);
        permission = !permission;
        num++;
        if (num%100==0)
            System.out.println();
        if (num+1==10000)
            stop=true;
        notifyAll();
    }
}
class SymbolSynchTest implements Runnable{
    char s;
    Sync sync;
    boolean controlValue;
    public  SymbolSynchTest (Sync sync, boolean controlValue, char symbol){
        s=symbol;
        this.controlValue=controlValue;
        this.sync = sync;
    }
    @Override
    public void run(){
        while (true){
            sync.waitAndChange(controlValue, s);
            if(sync.isStop())
                return;
        }
    }
}
public class main {
    public static void main(String[] args) {
//        myThread t1 = new myThread("t1", '-');
//        myThread t2 = new myThread("t2", '|');

        Sync s = new Sync();

        Thread t1 = new Thread(new SymbolSynchTest(s,true, '-'));
        Thread t2 = new Thread(new SymbolSynchTest(s,false, '|'));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        System.out.print("End");


    }
}
