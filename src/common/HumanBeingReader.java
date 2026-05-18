package common;

import common.models.HumanBeing;
import common.models.Car;
import common.models.Coordinates;
import common.models.WeaponType;

/**
 * Преобразует HumanBeing в CSV строку и обратно.
 * Используется для чтения/записи файлов данных.
 */
public class HumanBeingReader {

    /**
     * Создаёт объект Car из boolean значения
     * @param isCool флаг крутости машины
     * @return новый объект Car
     */
    private static Car convertCar(boolean isCool) {
        return new Car(isCool);
    }

    /**
     * Создаёт объект Coordinates из x и y
     * @param x координата X
     * @param y координата Y
     * @return новый объект Coordinates
     */
    private static Coordinates convertCoordinates(int x, long y) {
        return new Coordinates(x, y);
    }

    /**
     * Преобразует строку CSV в объект HumanBeing
     * @param line строка CSV с данными (формат из Const.FILEHEADER)
     * @return объект HumanBeing с данными из файла
     */
    public static HumanBeing convertLine(String line) {
        String[] humanInfo = line.split(",");

        Car car = convertCar(Boolean.parseBoolean(humanInfo[11]));
        Coordinates coordinates = convertCoordinates(
                Integer.parseInt(humanInfo[2]),
                Long.parseLong(humanInfo[3])
        );
        return new HumanBeing (
                Long.parseLong(humanInfo[0]),
                humanInfo[4],
                humanInfo[1],
                coordinates,
                Boolean.parseBoolean(humanInfo[5]),
                Boolean.parseBoolean(humanInfo[6]),
                Double.parseDouble(humanInfo[7]),
                humanInfo[8],
                Integer.parseInt(humanInfo[9]),
                humanInfo[10].isEmpty()? null:WeaponType.valueOf(humanInfo[10]),
                car
            );
        }

    /**
     * Преобразует объект HumanBeing в строку CSV
     * @param human объект для преобразования
     * @return строка CSV с данными объекта
     */
    public static String extractInfo(HumanBeing human) {
//        [BUG FIXED] Quy tắc của %b:
//        If passed arg is null/false => false.
//        If passed arg is not null/false (String, int, ...) => true.
//        format "%b": null/Boolean false => false | not null/false (String "false", int 0, ...) => true
        return String.format("%d,%s,%d,%d,%s,%b,%b,%s,%s,%d,%s,%b",
                human.getId(),
                human.getName(),
                human.getCoordinates().getX(),
                human.getCoordinates().getY(),
                human.getCreationDate(),
                human.isRealHero(),
                human.isHasToothpick(),
                "%s".formatted(human.getImpactSpeed()).replace(",", "."),
                human.getSoundtrackName(),
                human.getMinutesOfWaiting(),
                (human.getWeaponType() == null ? "" : human.getWeaponType()),
//                (human.getCar() == null ? "false" : human.getCar().isCool()) // null => true [%b of String "false" (not boolean false) is true]
                (human.getCar() != null && human.getCar().isCool())
        );
    }

    /**
     * Извлекает ID из строки CSV (первый элемент)
     * @param line строка CSV
     * @return ID объекта
     */
    public static long extractIdFromLine(String line) {
        return Long.parseLong(line.split(",")[0]);}

}

