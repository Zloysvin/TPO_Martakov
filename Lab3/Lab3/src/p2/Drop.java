package p2;

public class Drop {
    // Повідомлення, відправлене від виробника
    // до споживача.
    private int message;
    // true, якщо споживач повинен чекати,
    // поки виробник надішле повідомлення,
    // false, якщо виробник повинен чекати,
    // поки споживач отримає повідомлення.
    private boolean empty = true;

    public synchronized int take() {
        // Чекаємо, поки повідомлення буде доступне.
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Змінюємо статус.
        empty = true;
        // Повідомляємо виробника, що
        // статус змінився.
        notifyAll();
        return message;
    }

    public synchronized void put(int message) {
        // Чекаємо, поки повідомлення буде отримане.
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Змінюємо статус.
        empty = false;
        // Зберігаємо повідомлення.
        this.message = message;
        // Повідомляємо споживача, що статус
        // змінився.
        notifyAll();
    }
}
