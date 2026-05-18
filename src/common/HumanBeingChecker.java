package common;

import common.models.Car;
import common.models.Coordinates;
import common.models.WeaponType;
import common.models.Const;

/**
 * Проверяет корректность полей HumanBeing.
 * Все методы статические, выбрасывают IllegalArgumentException при неверных данных.
 */
public class HumanBeingChecker {
    public static String checkName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Поле 'name' не может быть null, строка не может быть пустой");
        }
        return name;
    }

    public static Coordinates checkCoordinates(Coordinates coordinates) {
        if (coordinates == null) {
            throw new IllegalArgumentException("Поле 'coordinates' не может быть null");
        }
        return coordinates;
    }

    public static Boolean checkIsRealHero(Boolean isRealHero) {
//        if (isRealHero != null && isRealHero) {
//            return true;
//        }
//        return false;
        return isRealHero != null && isRealHero;


//        Boolean(cases: null, false, true), boolean(cases: false, true)
//        Or return Boolean.TRUE.equals(isRealHero); null, false => false, true => true
//        return isRealHero != null; (is null or not)
    }

    public static Boolean checkIsHasToothpick(Boolean isHasToothpick) {
        return isHasToothpick!=null && isHasToothpick;
    }

    public static Double checkImpactSpeed(Double impactSpeed) {
        if (impactSpeed <= Const.MINVALUEIMPACTSPEED) {
            throw new IllegalArgumentException("Значение поля 'impactSpeed' должно быть больше " + Const.MINVALUEIMPACTSPEED + ". Получено: " + impactSpeed);
        }
        return impactSpeed;
    }

    public static String checkSoundtrackName(String soundtrackName) {
        if (soundtrackName.isEmpty()) {
            throw new IllegalArgumentException("Значение поля 'soundtrackName' не может быть null");
        }
        return soundtrackName;
    }

    public static int checkMinutesOfWaiting(int minutesOfWaiting) {
        if (minutesOfWaiting < 0) {
            throw new IllegalArgumentException("Значение поля 'minutesOfWaiting' должно быть больше нуля. Получено: " + minutesOfWaiting);
        }
        return minutesOfWaiting;
    }

    public static WeaponType checkWeaponType(WeaponType weaponType) {
        return weaponType;
    }

    public static Car checkCar(Car car) {
//        System.out.println(car.isCool()); // null
        return car;
    }
}
