package common.models;

/**
 * Класс Const содержит глобальные константы, используемые в приложении.
 * Он действует как централизованное хранилище конфигурации и изолированный узел в архитектуре, 
 * обеспечивая доступ к:
 * - Граничным значениям для полей объектов (MAXVALUEX, MINVALUEX и т.д.).
 * - Путям к файлам данных и скриптов (FILEPATH, SCRIPTFILEPATH).
 * - Настройкам сети (host, port) для взаимодействия ClientMain и ServerMain.
 * - Параметрам подключения к базе данных PostgreSQL (DB_URL, DB_USER_ENV, CLOUD_DB_URL и др.),
 *   которые используются в ServerMain для создания Connection.
 * 
 * Данный класс гарантирует единую точку настройки для клиентского и серверного приложений.
 */
public final class Const {
    public static final int MAXVALUEX = 128;
    public static final int MINVALUEX = -162;
    public static final long MAXVALUEY = 440L;
    public static final double MINVALUEIMPACTSPEED = -432d;
    public static final String FILEPATH = "data/data.csv";
    public static final String SCRIPTFILEPATH = "scripts/scripts.csv";
    public static int port = 1234;
//    public static String host = "localhost";
    public static String host = "127.0.0.1";
//    public static String host = "0.0.0.0";
//    public static String host = "192.268.10.80";

//    PostgreSQL


//    public static final String DB_HOST = "localhost";
//    public static final int DB_PORT = 5432;
//    public static final String DB_NAME = "humanbeings";
//    public static final String DB_URL = "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;
//    public static final String DB_USER_ENV = "DB_USER";
//    public static final String DB_PASSWORD_ENV = "DB_PASSWORD";
//    public static final String CLOUD_DB_URL = "jdbc:postgresql://aws-1-eu-west-2.pooler.supabase.com:6543/postgres";
//    public static final String CLOUD_DB_USER_ENV = "postgres.jpbfipdamfvisstxjjpz";
//    public static final String CLOUD_DB_PASSWORD_ENV = "javalab7pro";

    public static final String DB_HOST = System.getenv().getOrDefault("DB_HOST", "pg");
    public static final int DB_PORT = Integer.parseInt(System.getenv().getOrDefault("DB_PORT", "5432"));
    public static final String DB_NAME = System.getenv().getOrDefault("DB_NAME", "studs");

    public static final String DB_URL =
            "jdbc:postgresql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME;

    public static final String DB_USER_ENV = "DB_USER";
    public static final String DB_PASSWORD_ENV = "DB_PASSWORD";

    // Чек-лист для проверки строки (символ за символом):
    // Сначала идет: jdbc:postgresql:// (всего один раз).
    // Затем сразу адрес хоста: aws-1-eu-west-2.pooler.supabase.com
    // Затем двоеточие и порт: :6543
    // В конце слэш и имя базы данных: /postgres

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";

    // test for issue 3
//    public static final Long DEFAULT_USER_ID = 2L;

//  relative
//  getId, getName, getCoordinates, getCreationDate, isRealHero, isHasToothpick, getImpactSpeed, getSoundtrackName, getMinutesOfWaiting, getWeaponType, getCar
//  public static String[] PREFIXES = {"get", "is"};
    public static String[] FILEHEADER = {
        "Id",
        "Name",
        "CoordinateX",
        "CoordinateY",
        "CreationDate",
        "IsRealHero",
        "IsHasToothpick",
        "ImpactSpeed",
        "SoundtrackName",
        "MinutesOfWaiting",
        "WeaponType",
        "CarCool"
    };

//    absolute
    public static String[] FILEHEADERMETHODS = {
        "getId",
        "getName",
        "getCoordinates",
        "getCreationDate",
        "isRealHero",
        "isHasToothpick",
        "getImpactSpeed",
        "getSoundtrackName",
        "getMinutesOfWaiting",
        "getWeaponType",
        "getCar"
    };

    public static String cat = """

                                                  |\\__/,|   (`\\
                                                _.|o o  |_ _ ) )
                                              -(((---(((--------
        
            ___  ________  ___      ___ ________          ___       ________  ________          ________    \s
           |\\  \\|\\   __  \\|\\  \\    /  /|\\   __  \\        |\\  \\     |\\   __  \\|\\   __  \\        |\\   ____\\   \s
           \\ \\  \\ \\  \\|\\  \\ \\  \\  /  / | \\  \\|\\  \\       \\ \\  \\    \\ \\  \\|\\  \\ \\  \\|\\ /_       \\ \\  \\___|   \s
         __ \\ \\  \\ \\   __  \\ \\  \\/  / / \\ \\   __  \\       \\ \\  \\    \\ \\   __  \\ \\   __  \\       \\ \\  \\____  \s
        |\\  \\\\_\\  \\ \\  \\ \\  \\ \\    / /   \\ \\  \\ \\  \\       \\ \\  \\____\\ \\  \\ \\  \\ \\  \\|\\  \\       \\ \\  ___  \\\s
        \\ \\________\\ \\__\\ \\__\\ \\__/ /     \\ \\__\\ \\__\\       \\ \\_______\\ \\__\\ \\__\\ \\_______\\       \\ \\_______\\
         \\|________|\\|__|\\|__|\\|__|/       \\|__|\\|__|        \\|_______|\\|__|\\|__|\\|_______|        \\|_______|
        
        
        """;

    public static String java = """
                   █████   █████████   █████   █████   █████████      █████         █████████   ███████████      ████████\s
                  ░░███   ███░░░░░███ ░░███   ░░███   ███░░░░░███    ░░███         ███░░░░░███ ░░███░░░░░███    ███░░░░███
                   ░███  ░███    ░███  ░███    ░███  ░███    ░███     ░███        ░███    ░███  ░███    ░███   ░███   ░░░\s
                   ░███  ░███████████  ░███    ░███  ░███████████     ░███        ░███████████  ░██████████    ░█████████\s
                   ░███  ░███░░░░░███  ░░███   ███   ░███░░░░░███     ░███        ░███░░░░░███  ░███░░░░░███   ░███░░░░███
             ███   ░███  ░███    ░███   ░░░█████░    ░███    ░███     ░███      █ ░███    ░███  ░███    ░███   ░███   ░███
            ░░████████   █████   █████    ░░███      █████   █████    ███████████ █████   █████ ███████████    ░░████████\s
             ░░░░░░░░   ░░░░░   ░░░░░      ░░░      ░░░░░   ░░░░░    ░░░░░░░░░░░ ░░░░░   ░░░░░ ░░░░░░░░░░░      ░░░░░░░░ \s
        """;

    public static String bears = """
           _     _      _     _      _     _      _     _       _     _      _     _      _     _    6
                  (c).-.(c)    (c).-.(c)    (c).-.(c)    (c).-.(c)     (c).-.(c)    (c).-.(c)    (c).-.(c)   \s
                   / ._. \\      / ._. \\      / ._. \\      / ._. \\       / ._. \\      / ._. \\      / ._. \\    \s
                 __\\( Y )/__  __\\( Y )/__  __\\( Y )/__  __\\( Y )/__   __\\( Y )/__  __\\( Y )/__  __\\( Y )/__  \s
                (_.-/'-'\\-._)(_.-/'-'\\-._)(_.-/'-'\\-._)(_.-/'-'\\-._) (_.-/'-'\\-._)(_.-/'-'\\-._)(_.-/'-'\\-._) \s
                   || J ||      || A ||      || V ||      || A ||       || L ||      || A ||      || B ||    \s
                 _.' `-' '._  _.' `-' '._  _.' `-' '._  _.' `-' '._   _.' `-' '._  _.' `-' '._  _.' `-' '._  \s
                (.-./`-'\\.-.)(.-./`-'\\.-.)(.-./`-'\\.-.)(.-./`-'\\.-.) (.-./`-'\\.-.)(.-./`-'\\.-.)(.-./`-'\\.-.) \s
                 `-'     `-'  `-'     `-'  `-'     `-'  `-'     `-'   `-'     `-'  `-'     `-'  `-'     `-'  \s
    """;
}
