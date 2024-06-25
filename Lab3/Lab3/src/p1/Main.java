package p1; /**
 author Cay Horstmann
 */
import java.util.concurrent.locks.ReentrantLock;
class AsynchBankTest {
    public static final int NACCOUNTS = 10;
    public static final int INITIAL_BALANCE = 10000;
    public static void main(String[] args) {
        Bank b = new Bank(NACCOUNTS, INITIAL_BALANCE);
        int i;
        for (i = 0; i < NACCOUNTS; i++){
            TransferThread t = new TransferThread(b, i,
                    INITIAL_BALANCE);
            t.setPriority(Thread.NORM_PRIORITY + i % 2);
            t.start () ;
        }
    }
}
class Bank {
    public static final int NTEST = 10000;
    private final int[] accounts;
    private long ntransacts = 0;
    private Object obj = new Object();
    ReentrantLock locker = new ReentrantLock();


    public Bank(int n, int initialBalance){
        accounts = new int[n];
        int i;
        for (i = 0; i < accounts.length; i++)
            accounts[i] = initialBalance;
        ntransacts = 0;
    }
    public void transfer(int from, int to, int amount) {
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        if (ntransacts % NTEST == 0)
            test();
    }

    public synchronized void sync_method_transfer(int from, int to, int amount) {
        try {
            while (accounts[from] < amount)
                wait();
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        notifyAll() ;
        if (ntransacts % NTEST == 0)
            test();
    }

    public void sync_block_transfer(int from, int to, int amount) {
        synchronized (obj) {
            try {
                while (accounts[from] < amount)
                    obj.wait();
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
            accounts[from] -= amount;
            accounts[to] += amount;
            ntransacts++;
            obj.notifyAll() ;
            if (ntransacts % NTEST == 0)
                test();
        }
    }

    public synchronized void sync_reentrantlock_transfer(int from, int to, int amount) {
        locker.lock();
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        if (ntransacts % NTEST == 0)
            test();
        locker.unlock();
    }

    public void test(){
        int sum = 0;
        for (int i = 0; i < accounts.length; i++)
            sum += accounts[i] ;
        System.out.println("Transactions:" + ntransacts
                + " Sum: " + sum);
    }
    public int size(){
        return accounts.length;
    }
}
class TransferThread extends Thread {
    private Bank bank;
    private int fromAccount;
    private int maxAmount;
    private static final int REPS = 1000;
    public TransferThread(Bank b, int from, int max){
        bank = b;
        fromAccount = from;
        maxAmount = max;
    }
    @Override
    public void run(){
        while (true) {
            for (int i = 0; i < REPS; i++) {
                int toAccount = (int) (bank.size() * Math.random());
                int amount = (int) (maxAmount * Math.random()/REPS);
                //bank.transfer(fromAccount, toAccount, amount);
                //bank.sync_method_transfer(fromAccount, toAccount, amount);
                //bank.sync_block_transfer(fromAccount, toAccount, amount);
                //bank.sync_reentrantlock_transfer(fromAccount, toAccount, amount);
            }
        }
    }
}

////TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
//// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
//public class Main {
//    public static void main(String[] args) {
//        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
//        // to see how IntelliJ IDEA suggests fixing it.
//        System.out.printf("Hello and welcome!");
//
//        for (int i = 1; i <= 5; i++) {
//            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
//            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
//            System.out.println("i = " + i);
//        }
//    }
//}