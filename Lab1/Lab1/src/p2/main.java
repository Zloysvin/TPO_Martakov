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

    // Статичний метод (не пов'язаний з конкретним об'єктом)
    public static void f() {
        int a = 0;
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
    public static double l (int x1, int y1, int x2, int y2){
        return Math.pow(Math.pow(x2-x1, 2)+Math.pow(y2-y1, 2), 0.5);
    }

    public boolean goal(){
        if (l(20,20, x, y)<=10 ||
                l(this.canvas.getWidth()-20,20, x, y)<=10 ||
                l(this.canvas.getWidth()-20,this.canvas.getHeight()-20, x, y)<=10 ||
                l(20,this.canvas.getHeight()-20, x, y)<=10) {
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

// Лістинг класу BallThread (Потік для м'яча)
class BallThread extends Thread {
    private Ball b; // М'яч, який буде рухатися
    private BallCanvas canvas; // Посилання на BallCanvas

    // Конструктор класу BallThread
    public BallThread(Ball ball, BallCanvas canvas) {
        b = ball;
        this.canvas = canvas; // Ініціалізація посилання на BallCanvas
    }

    // Метод, який виконується при запуску потоку
    @Override
    public void run() {
        try {
            // Безкінечний цикл для руху м'яча
            for (int i = 1; i < 10000; i++) {
                if (b.goal()) {
                    canvas.del(b);
                    synchronized(canvas){Ball.count++;}
                    // Оновлення текстового поля при зміні значення count
                    ((BounceFrame) SwingUtilities.getWindowAncestor(canvas)).updateCountTextField();
                    b.move();
                    break;
                }
                b.move();
                System.out.println("Thread name = " + Thread.currentThread().getName());
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

    private JTextField countTextField; // Додано текстове поле
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

        // Додавання текстового поля
        countTextField = new JTextField("0");
        countTextField.setEditable(false);
        content.add(countTextField, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.lightGray);
        JButton buttonStart = new JButton("Start"); // Кнопка для запуску м'яча
        JButton buttonStop = new JButton("Stop"); // Кнопка для завершення програми

        // Обробник події для кнопки "Start"
        buttonStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Ball b = new Ball(canvas);
                canvas.add(b);
                BallThread thread = new BallThread(b, canvas);
                thread.start(); // Запуск потоку для руху м'яча
                System.out.println("Thread name =" + thread.getName());
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
    // Оновлення значення текстового поля countTextField
    public void updateCountTextField() {
        countTextField.setText(String.valueOf(Ball.count));
    }
}

// Лістинг класу Bounce (Головний клас)
class Bounce {
    public static void main(String[] args) {
        BounceFrame frame = new BounceFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true); // Виведення вікна на екран
        System.out.println("Thread name =" + Thread.currentThread().getName());
    }
}