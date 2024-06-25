package p5;
import java.util.logging.Level;
import java.util.logging.Logger;

class myThread extends Thread{
    char symbol;

    myThread(String name, char symbol){
        super(name);
        this.symbol = symbol;
    }

    public void run(){
        for (int i = 0; i < 100; i++){
            for(int j = 0; j < 100; j++){
                System.out.print(symbol);
            }
            System.out.print('\n');
        }
    }
}

class Sync{
    private boolean permission;
    private int count;
    private boolean stop;
    public Sync(){
        permission = true;
        count = 0;
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
        count++;
        if (count % 100 == 0)
            System.out.println();
        if (count + 1 == 10000)
            stop = true;
        notifyAll();
    }
}
class SymbolSynchTest implements Runnable{
    char symbol;
    Sync sync;
    boolean controlValue;

    public  SymbolSynchTest (Sync sync, boolean controlValue, char symbol){
        this.symbol = symbol;
        this.controlValue = controlValue;
        this.sync = sync;
    }

    @Override
    public void run(){
        while (true){
            sync.waitAndChange(controlValue, symbol);
            if(sync.isStop())
                return;
        }
    }
}
public class main {
    public static void main(String[] args) {
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
