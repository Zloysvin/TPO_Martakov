package p3;

public class jornal {
    public String [][][][] mas = new String [3][20][12][2]; // група, студент, тиждень, лектор чи асистент

    public synchronized void put (int group, int student, int week, boolean isLector, String grade){
        mas[group][student][week][isLector ? 1 : 0]=grade;
    }
    public void print() {
        for(int gro = 0; gro < 3; gro ++) {
            System.out.print("\n Group "+gro+":\n");
            for (int st = 0; st < 20; st++) {
                System.out.print("\n Student "+st+":\n");
                for (int l=0; l<2; l++){
                    if (l==0) {
                        System.out.print("As: ");
                    }
                    else System.out.print("\nLe: ");
                    for (int w = 0; w < 12; w++) {
                        System.out.print(mas[gro][st][w][l]+" ");
                    }
                }
            }
        }
    }
}
