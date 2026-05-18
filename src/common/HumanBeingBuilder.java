package common;

import common.models.Car;
import common.models.Coordinates;
import common.models.HumanBeing;
import common.models.WeaponType;
import common.models.Const;
import common.utils.BooleanBuilder;

import java.util.Scanner;

public class HumanBeingBuilder {
    private final Scanner scanner;
    private final Coordinates coordinatesChecker;
    private final BooleanBuilder booleanBuilder;

    public HumanBeingBuilder(Scanner sc, Coordinates coordinatesChecker, BooleanBuilder booleanBuilder) {
        this.scanner=sc;
        this.coordinatesChecker=coordinatesChecker;
        this.booleanBuilder=booleanBuilder;
    }

    // read name
    public String readName() {
        while (true) {
            System.out.print("Введите имя: ");

            String input = scanner.nextLine().trim();

            try {
                HumanBeingChecker.checkName(input);
            } catch (IllegalArgumentException E) {
                System.out.printf("Ошибка: %s\n", E.getMessage());
                continue;
            }
            return input;
        }
    }

    // read Координаты
    // # X
    public int readX() {
        while (true) {
            System.out.printf("Введите координату X (X > %d): ", Const.MINVALUEX);
            String input = scanner.nextLine().trim();

            try {
                int x = Integer.parseInt(input);

                try {
                    coordinatesChecker.setX(x);
                } catch (IllegalArgumentException E) {
                    System.out.printf("Ошибка: %s\n", E.getMessage());
                    continue;
                }

                return x;

            } catch (NumberFormatException e) {

                System.out.println("Ошибка: введите целое число!");
            }
        }
    }

    // # Y

    public long readY() {
        while (true) {
            System.out.printf("Введите координату Y (Макс. %d): ", Const.MAXVALUEY);

            String input = scanner.nextLine().trim();

            try {
                long y = Long.parseLong(input);
                try {
                    coordinatesChecker.setY(y);
                } catch (IllegalArgumentException E) {
                    System.out.printf("Ошибка: %s\n", E.getMessage());
                    continue;
                }
                return y;

            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число (типа long)!");
            }
        }
    }

    // realHero ??
    public Boolean readBoolean(String message) {
        boolean isRealHero = this.booleanBuilder.readBoolean(message);
        return HumanBeingChecker.checkIsRealHero(isRealHero);
    }

    // # impactSpeed
    public double readImpactSpeed() {
        while (true) {
            System.out.printf("Введите скорость удара (impactSpeed > %f): ", Const.MINVALUEIMPACTSPEED);
            String input = scanner.nextLine().trim();
            try {
                double speed = Double.parseDouble(input);

                try {
                    HumanBeingChecker.checkImpactSpeed(speed);
                    return speed;
                } catch (IllegalArgumentException E) {
                    System.out.printf("Ошибка: %f\n", Const.MINVALUEIMPACTSPEED);
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите число (double)!");
            }
        }
    }

    // # WeaponType
    public WeaponType readWeaponType() {
        while (true) {
            System.out.println("Выберите тип оружия: HAMMER, RIFLE, KNIFE, MACHINE_GUN");
            System.out.print("Оставьте пустым для null: ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.isEmpty()) return null;

            try {
                return HumanBeingChecker.checkWeaponType(WeaponType.valueOf(input)); // يحول النص إلى قيمة من الـ Enum
            } catch (IllegalArgumentException e) {
                System.out.printf("Ошибка: %s\n", e.getMessage());
            }
        }
    }

    public Car readCar() {
        while (true) {
            System.out.print("У него есть машина? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();

            if (answer.equals("yes")) {
                boolean isCool = readBoolean("Машина крутая?");
                try {
                    return HumanBeingChecker.checkCar(new Car(isCool)); // ننشئ كائن سيارة جديد
                } catch (Exception e) {
                    System.out.printf("Ошибка: %s\n", e.getMessage());
                }
            } else if (answer.equals("no")){
                return null;
            }
        }
//            return null; // إذا قال لا، نرجع null (لا توجد سيارة)
    }
    // # read Sound track Name
    public String readSoundtrackName() {
        while (true) {
            System.out.print("Введите название саундтрека: ");
            String input = scanner.nextLine().trim();
            try {
                return HumanBeingChecker.checkSoundtrackName(input);
            } catch (IllegalArgumentException e) {
                System.out.printf("Ошибка: %s\n", e.getMessage());
            }
        }
    }
    // # read Minutes Of Waiting
    public int readMinutesOfWaiting() {
        while (true) {
            System.out.print("Введите время ожидания (минуты): ");
            String input = scanner.nextLine().trim();
            try {
                Integer.parseInt(input);
                try {
                    return HumanBeingChecker.checkMinutesOfWaiting(Integer.parseInt(input));
                } catch (IllegalArgumentException e) {
                    System.out.printf("Ошибка: %s\n", e.getMessage());
                }
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: введите целое число!");
            }
        }
    }

    // # readHumanBeing()
    public HumanBeing readHumanBeing() {

        String name = readName();
        Coordinates coors = new Coordinates(readX(), readY());
        boolean hero = readBoolean("Он настоящий герой?");
        boolean toothpick = readBoolean("У него есть зубочистка?");
        double speed = readImpactSpeed();

        String soundtrack = readSoundtrackName();
        int minutes = readMinutesOfWaiting();
        WeaponType weapon = readWeaponType();
        Car car = readCar();
//        System.out.println(car.isCool()); // null
        return new HumanBeing(name, coors, hero, toothpick, speed, soundtrack, minutes, weapon, car);
    }

    public HumanBeing readHumanBeing(String[] args) {
//        args: ["name", "x", "y", ...]
        try {
            HumanBeing humanBeing = new HumanBeing(
                    args[0],
                    args[1],
                    args[2],
                    args[3],
                    args[4],
                    args[5],
                    args[6],
                    args[7],
                    args[8],
                    args[9]
            );
            return humanBeing;
        } catch (Exception e) {
            throw new IllegalArgumentException("Some args went wrong");
        }
    }


    /**
     * Создаёт HumanBeing с указанным ID (через конвертацию в CSV и обратно)
     * @param id желаемый ID
     * @return HumanBeing с указанным ID
     */
    public HumanBeing createHumanBeingWithId(long id) {
//        HumanBeing human = readHumanBeing();
//        String humanInfo = HumanBeingReader.extractInfo(human);
//        String[] infoFields = humanInfo.split(",");
//        infoFields[0] = String.valueOf(id);
//        return HumanBeingReader.convertLine(String.join(",", infoFields));
        return createHumanBeingWithId(id, null);
    }

    public HumanBeing createHumanBeingWithId(long id, String[] args) {
        HumanBeing human = args==null? readHumanBeing():readHumanBeing(args);

        String humanInfo = HumanBeingReader.extractInfo(human);
        String[] infoFields = humanInfo.split(",");
        infoFields[0] = String.valueOf(id);
        return HumanBeingReader.convertLine(String.join(",", infoFields));
    }
}
