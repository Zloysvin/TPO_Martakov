package p3;
import java.util.Random;
public class producer implements Runnable{
    private jornal j;
    private String name;
    private int[] groups;
    private boolean isLector;

    public producer(jornal j, String name, boolean isLector, int[] groups){
        this.j=j;
        this.name = name;
        this.groups = groups;
        this.isLector = isLector;
    }
    
    public void run () {
        Random random = new Random();
        for (int g : groups){
            for(int stud=0; stud<20; stud ++){
                for(int w=0; w<12; w++) {
                    j.put(g, stud, w, isLector, random.nextInt(100) + " (" + name+ ");");
                }
            }
        }
    }
}
