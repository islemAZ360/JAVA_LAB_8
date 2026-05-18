package common.models;

import common.HumanBeingChecker;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

import java.io.Serializable;

public class HumanBeing implements Serializable, Comparable<HumanBeing> {

    private static final long serialVersionUID = 1L;

    private static AtomicLong humanCount = new AtomicLong(0); // The first ID is 1
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private java.time.LocalDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private boolean realHero;
    private boolean hasToothpick;
    private double impactSpeed; //Значение поля должно быть больше -432
    private String soundtrackName; //Поле не может быть null
    private int minutesOfWaiting;
    private WeaponType weaponType; //Поле может быть null
    private Car car; //Поле может быть null

    // Private для того, чтобы HumanBeing не можно создать без аргументов
    private HumanBeing() {
        //Automatic value and cant set value
        this.id = humanCount.incrementAndGet();
        this.creationDate = java.time.LocalDateTime.now();
    }

    public HumanBeing(
        String name,
        Coordinates coordinates,
        boolean realHero,
        boolean hasToothpick,
        double impactSpeed,
        String soundtrackName,
        int minutesOfWaiting,
        WeaponType weaponType,
        Car car
    ) {
        this();
        this.setName(name);
        this.setCoordinates(coordinates);
        this.setRealHero(realHero);
        this.setHasToothpick(hasToothpick);
        this.setImpactSpeed(impactSpeed);
        this.setSoundtrackName(soundtrackName);
        this.setMinutesOfWaiting(minutesOfWaiting);
        this.setWeaponType(weaponType);
        this.setCar(car);
    }
    public HumanBeing(
            long id,
            String creationDateVal,
            String name,
            Coordinates coordinates,
            boolean realHero,
            boolean hasToothpick,
            double impactSpeed,
            String soundtrackName,
            int minutesOfWaiting,
            WeaponType weaponType,
            Car car
    ) {
        this.setId(id);
        this.setValueCreationDate(creationDateVal);
        this.setName(name);
        this.setCoordinates(coordinates);
        this.setRealHero(realHero);
        this.setHasToothpick(hasToothpick);
        this.setImpactSpeed(impactSpeed);
        this.setSoundtrackName(soundtrackName);
        this.setMinutesOfWaiting(minutesOfWaiting);
        this.setWeaponType(weaponType);
        this.setCar(car);
    }

    public HumanBeing(
        String name,
        String coorX,
        String coorY,
        String realHero,
        String hasToothpick,
        String impactSpeed,
        String soundtrackName,
        String minutesOfWaiting,
        String weaponType,
        String car
    ) {
        this();
        this.setName(name);
        this.setCoordinates(new Coordinates(Integer.parseInt(coorX), Long.parseLong(coorY)));
        this.setRealHero(Boolean.parseBoolean(realHero));
        this.setHasToothpick(Boolean.parseBoolean(hasToothpick));
        this.setImpactSpeed(Double.parseDouble(impactSpeed));
        this.setSoundtrackName(soundtrackName);
        this.setMinutesOfWaiting(Integer.parseInt(minutesOfWaiting));
        this.setWeaponType(WeaponType.valueOf(weaponType));
        this.setCar(new Car(Boolean.parseBoolean(car)));
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setValueHumanCount(long value){
        HumanBeing.humanCount.set(value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = HumanBeingChecker.checkName(name);
    }

    public Coordinates getCoordinates() {
        return this.coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = HumanBeingChecker.checkCoordinates(coordinates);
    }

    public java.time.LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setValueCreationDate(String creationDateVal) {
        this.creationDate = LocalDateTime.parse(creationDateVal);
    }

    public boolean isRealHero() {
        return realHero;
    }

    public void setRealHero(boolean realHero) {
        this.realHero = HumanBeingChecker.checkIsRealHero(realHero);
    }

    public boolean isHasToothpick() {
        return hasToothpick;
    }

    public void setHasToothpick(boolean hasToothpick) {
        this.hasToothpick = HumanBeingChecker.checkIsHasToothpick(hasToothpick);
    }

    public double getImpactSpeed() {
        return impactSpeed;
    }

    public void setImpactSpeed(double impactSpeed) {
        this.impactSpeed = HumanBeingChecker.checkImpactSpeed(impactSpeed);
    }

    public String getSoundtrackName() {
        return soundtrackName;
    }

    public void setSoundtrackName(String soundtrackName) {
        this.soundtrackName = HumanBeingChecker.checkSoundtrackName(soundtrackName);
    }

    public int getMinutesOfWaiting() {
        return minutesOfWaiting;
    }

    public void setMinutesOfWaiting(int minutesOfWaiting) {
        this.minutesOfWaiting = HumanBeingChecker.checkMinutesOfWaiting(minutesOfWaiting);
    }

    public WeaponType getWeaponType() {
        return weaponType;
    }

    public void setWeaponType(WeaponType weaponType) {
        this.weaponType = HumanBeingChecker.checkWeaponType(weaponType);
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = HumanBeingChecker.checkCar(car);
    }

    @Override
    public boolean equals(Object newHuman) {
        if(this == newHuman) { return true; };
        if (newHuman == null || this.getClass() != newHuman.getClass()) { return false; };
        HumanBeing thatHuman = (HumanBeing) newHuman;
        return id == thatHuman.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public int compareTo(HumanBeing comparingHuman) {
        return Long.compare(this.id, comparingHuman.getId());
//        return this.getId().compareTo(comparingHuman.getId());
    }
    @Override
    public String toString() {
        return "HumanBeing {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=(" + coordinates.getX() + ", " + coordinates.getY() + ")" +
                ", realHero=" + realHero +
                ", hasToothpick=" + hasToothpick +
                ", impactSpeed=" + impactSpeed +
                ", soundtrackName='" + soundtrackName + '\'' +
                ", minutesOfWaiting=" + minutesOfWaiting +
                ", weaponType=" + weaponType +
                ", car=" + (car != null ? car.isCool() : null) +
                ", creationDate=" + creationDate +
                '}';
    }
}
