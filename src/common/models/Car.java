package common.models;

import java.io.Serializable;


public class Car implements Serializable {

    private Boolean cool; //Поле не может быть null

    /**
     * Создаёт объект Car с проверкой на null
     * @param cool true/false - крутая ли машина
     */

    public Car(Boolean cool) {
        this.setCar(cool);
    }

    public boolean isCool() {
        return this.cool;
    }

    public void setCar(Boolean cool) {
        if (cool == null) throw new IllegalArgumentException("Cool can not be null!");
        this.cool = cool;
    }
}

