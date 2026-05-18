package common.models;

import java.io.Serializable;


public class Coordinates implements Serializable {


    private int x; //Значение поля должно быть больше -162
    private long y; //Максимальное значение поля: 440

    public Coordinates() {}

    /**
     * Создаёт координаты с проверкой допустимых значений
     * @param x координата X (должна быть > Const.MINVALUEX и < Const.MAXVALUEX)
     * @param y координата Y (должна быть <= Const.MAXVALUEY)
     */

    public Coordinates(int x, long y) {
        setX(x);
        setY(y);
    }

    public int getX() {
        return x;
    }

    public long getY() {
        return y;
    }

    public void setX(int x) {
        if (x <= Const.MINVALUEX || x >= Const.MAXVALUEX) {
            throw new IllegalArgumentException("Значение поля X должно быть больше " + Const.MINVALUEX + " и меньше " + Const.MAXVALUEX + ". Получено: " + x);
        }
        this.x = x;
    }

    public void setY(long y) {
        if (y > Const.MAXVALUEY) {
            throw new IllegalArgumentException("Максимальное значение поля Y: " + Const.MAXVALUEY + ". Получено: " + y);
        }
        this.y = y;
    }
}
