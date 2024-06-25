package p2;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

// Лістинг класу Ball (Клас м'яча)
class Ball {
    private Component canvas; // Компонент, на якому малюється м'яч
    private static final int XSIZE = 20; // Розмір м'яча по горизонталі
    private static final int YSIZE = 20; // Розмір м'яча по вертикалі
    private int x = 0; // Поточна координата x м'яча
    private int y = 0; // Поточна координата y м'яча
    private int dx = 2; // Зміна x при русі м'яча
    private int dy = 2; // Зміна y при русі м'яча

    public static int count = 0;

    // Конструктор класу Ball
    public Ball(Component c) {
        this.canvas = c;
        // Випадкове визначення початкових координат м'яча
        if (Math.random() < 0.5) {
            x = new Random().nextInt(this.canvas.getWidth());
            y = 0;
        } else {
            x = 0;
            y = new Random().nextInt(this.canvas.getHeight());
        }
    }

    // Метод для малювання м'яча
    public void draw(Graphics2D g2) {
        // Малюємо м'яч
        g2.setColor(Color.darkGray);
        g2.fill(new Ellipse2D.Double(x, y, XSIZE, YSIZE));
    }

    // Метод для руху м'яча
    public void move() {
        x += dx;
        y += dy;

        // Обробка зіткнень м'яча з краями вікна
        if (x < 0) {
            x = 0;
            dx = -dx;
        }
        if (x + XSIZE >= this.canvas.getWidth()) {
            x = this.canvas.getWidth() - XSIZE;
            dx = -dx;
        }
        if (y < 0) {
            y = 0;
            dy = -dy;
        }
        if (y + YSIZE >= this.canvas.getHeight()) {
            y = this.canvas.getHeight() - YSIZE;
            dy = -dy;
        }

        // Перемальовування вікна
        this.canvas.repaint();
    }
    public static double distance (int x1, int y1, int x2, int y2){
        return Math.pow(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2), 0.5);
    }

    public boolean goal(){
        if (distance(20,20, x, y)<=10 ||
                distance(this.canvas.getWidth()-20,20, x, y)<=10 ||
                distance(this.canvas.getWidth()-20,this.canvas.getHeight()-20, x, y)<=10 ||
                distance(20,this.canvas.getHeight()-20, x, y)<=10) {
            return true;
        } else return false;
    }

}

// Лістинг класу BallCanvas (Панель для м'ячів)
class BallCanvas extends JPanel {
    private ArrayList<Ball> balls = new ArrayList<>(); // Список м'ячів, які відображаються на панелі

    // Метод для додавання м'яча до списку
    public void add(Ball b) {
        this.balls.add(b);
    }

    public void del(Ball b){
        this.balls.remove(b);
    }

    // Метод для малювання всіх м'ячів на панелі
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < balls.size(); i++) {
            Ball b = balls.get(i);
            b.draw(g2);
        }
        // Малюємо лузи у кутах
        drawCornerPocket(g2, 0, 0); // Лівий верхній кут
        drawCornerPocket(g2, getWidth() - 40, 0); // Правий верхній кут
        drawCornerPocket(g2, 0, getHeight() - 40); // Лівий нижній кут
        drawCornerPocket(g2, getWidth() - 40, getHeight() - 40); // Правий нижній кут
    }

    private void drawCornerPocket(Graphics2D g2, int x, int y) {
        int pocketSize = 40; // Розмір лузи (можна змінювати за потреби)
        g2.setColor(Color.BLACK);
        g2.fill(new Ellipse2D.Double(x, y, pocketSize, pocketSize));
    }
}

class BallThread extends Thread {
    private Ball b;
    private BallCanvas canvas;

    public BallThread(Ball ball, BallCanvas canvas) {
        b = ball;
        this.canvas = canvas;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i < 10000; i++) {
                if (b.goal()) {
                    canvas.del(b);
                    synchronized(canvas){Ball.count++;}
                    ((BounceFrame) SwingUtilities.getWindowAncestor(canvas)).updateCountTextField();
                    b.move();
                    break;
                }
                b.move();
                System.out.println("Thread name = " + Thread.currentThread().getName());
                Thread.sleep(5);
            }
        } catch (InterruptedException ex) {
        }
    }
}

class BounceFrame extends JFrame {
    private BallCanvas canvas;

    private JTextField countTextField;
    public static final int WIDTH = 450;
    public static final int HEIGHT = 350;

    public BounceFrame() {
        this.setSize(WIDTH, HEIGHT);
        this.setTitle("Bounce programm");
        this.canvas = new BallCanvas();
        System.out.println("In Frame Thread name = " + Thread.currentThread().getName());
        Container content = this.getContentPane();
        content.add(this.canvas, BorderLayout.CENTER);

        countTextField = new JTextField("0");
        countTextField.setEditable(false);
        content.add(countTextField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);
        JButton buttonStart = new JButton("Start");
        JButton buttonStop = new JButton("Stop");

        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ball b = new Ball(canvas);
                canvas.add(b);
                BallThread thread = new BallThread(b, canvas);
                thread.start();
                System.out.println("Thread name =" + thread.getName());
            }
        });

        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonStop);
        content.add(buttonPanel, BorderLayout.SOUTH);
    }

    public void updateCountTextField() {
        countTextField.setText(String.valueOf(Ball.count));
    }
}

class Bounce {
    public static void main(String[] args) {
        BounceFrame frame = new BounceFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        System.out.println("Thread name =" + Thread.currentThread().getName());
    }
}