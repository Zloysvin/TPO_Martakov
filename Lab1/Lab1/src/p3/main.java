package p3;
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

// Лістинг класу Ball (Клас м'яча)
class Ball {
    private Component canvas; // Компонент, на якому малюється м'яч
    private static final int XSIZE = 20; // Розмір м'яча по горизонталі
    private static final int YSIZE = 20; // Розмір м'яча по вертикалі
    private int x = 0; // Поточна координата x м'яча
    private int y = 0; // Поточна координата y м'яча
    private int dx = 2; // Зміна x при русі м'яча
    private int dy = 2; // Зміна y при русі м'яча

    private boolean blue = true;

    // Конструктор класу Ball
    public Ball(Component c, boolean blue) {
        this.blue = blue;
        this.canvas = c;
        // Випадкове визначення початкових координат м'яча
//        if (Math.random() < 0.5) {
//            x = new Random().nextInt(this.canvas.getWidth());
//            y = 0;
//        } else {
//            x = 0;
//            y = new Random().nextInt(this.canvas.getHeight());
//        }
        x=10;
        y=20;
    }

    // Статичний метод (не пов'язаний з конкретним об'єктом)
    public static void f() {
        int a = 0;
    }

    // Метод для малювання м'яча
    public void draw(Graphics2D g2) {
        if (this.blue) {g2.setColor(Color.blue);}
        else g2.setColor(Color.red);
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
}

// Лістинг класу BallCanvas (Панель для м'ячів)
class BallCanvas extends JPanel {
    private ArrayList<Ball> balls = new ArrayList<>(); // Список м'ячів, які відображаються на панелі

    // Метод для додавання м'яча до списку
    public void add(Ball b) {
        this.balls.add(b);
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
    }
}

// Лістинг класу BallThread (Потік для м'яча)
class BallThread extends Thread {
    private Ball b; // М'яч, який буде рухатися

    // Конструктор класу BallThread
    public BallThread(Ball ball, boolean blue) {
        if (blue) setPriority(1);
        else setPriority(10);
        b = ball;
    }

    // Метод, який виконується при запуску потоку
    @Override
    public void run() {
        try {
            // Безкінечний цикл для руху м'яча
            for (int i = 1; i < 10000; i++) {
                b.move();
                //System.out.println("Thread name = " + Thread.currentThread().getName());
                Thread.sleep(5); // Затримка для плавності руху
            }
        } catch (InterruptedException ex) {
            // Обробка виняткової ситуації (переривання потоку)
        }
    }
}

// Лістинг класу BounceFrame (Головне вікно програми)
class BounceFrame extends JFrame {
    private BallCanvas canvas; // Панель для відображення м'ячів
    public static final int WIDTH = 450; // Ширина вікна
    public static final int HEIGHT = 350; // Висота вікна

    // Конструктор класу BounceFrame
    public BounceFrame() {
        this.setSize(WIDTH, HEIGHT);
        this.setTitle("Bounce programm");
        this.canvas = new BallCanvas(); // Створення панелі для відображення м'ячів
        System.out.println("In Frame Thread name = " + Thread.currentThread().getName());
        Container content = this.getContentPane();
        content.add(this.canvas, BorderLayout.CENTER); // Додавання панелі на вікно

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);
        JButton buttonStart = new JButton("Start"); // Кнопка для запуску м'яча
        JButton buttonStop = new JButton("Stop"); // Кнопка для завершення програми

        // Обробник події для кнопки "Start"
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i<Bounce.ksk; i++){
                    Ball b = new Ball(canvas, true);
                    canvas.add(b);
                    BallThread thread = new BallThread(b, true);
                    thread.start(); // Запуск потоку для руху м'яча
                    //System.out.println("Thread name =" + thread.getName());
                }
                Ball b = new Ball(canvas, false);
                canvas.add(b);
                BallThread thread = new BallThread(b, false);
                thread.start(); // Запуск потоку для руху м'яча
                //System.out.println("Thread name =" + thread.getName());
            }
        });

        // Обробник події для кнопки "Stop"
        buttonStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Завершення програми
            }
        });

        buttonPanel.add(buttonStart);
        buttonPanel.add(buttonStop);
        content.add(buttonPanel, BorderLayout.SOUTH); // Додавання панелі з кнопками на вікно
    }
}

// Лістинг класу Bounce (Головний клас)
class Bounce {
    public static int ksk = 3000; //кількість синіх кульок
    public static void main(String[] args) {
        BounceFrame frame = new BounceFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); // Виведення вікна на екран
        //System.out.println("Thread name =" + Thread.currentThread().getName());
    }
}

