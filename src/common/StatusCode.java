package common;

import java.io.Serializable;

/**
 * Этот Enum является основой системы.
 * Не удалять и не изменять без согласования!
 */

public enum StatusCode implements Serializable {
    // Group 2xx: Success
    OK(200, "Операция выполнена успешно"),
    CREATED(201, "Объект успешно создан"),
    CONTINUE(202, "ID валиден. Пожалуйста, продолжите ввод данных"),

    // Group 4xx: Client errors
    BAD_REQUEST(400, "Некорректный запрос. Данные имеют неверную структуру"),
    ID_INVALID(401, "ID не соответствует условиям"),
    ID_TAKEN(402, "Данный ID уже занят"),
    NAME_INVALID(403, "Имя не соответствует условиям"),
    FORMAT_INVALID(404, "Неверный формат данных (ошибка типа данных)"),
    OUT_OF_RANGE(405, "Значение вне допустимого диапазона"),
    REQUIRED_FIELD_MISSING(406, "Отсутствуют обязательные поля"),

    // Group 5xx: Server or System errors
    SERVER_ERROR(500, "Внутренняя ошибка сервера. Попробуйте позже"),
    SERVICE_UNAVAILABLE(503, "Сервис временно недоступен"),
    TIMEOUT(504, "Время ожидания истекло. Соединение закрыто");

    private final int code;
    private final String description;

    StatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    // quick check success util
    public boolean isSuccess() {
        return this.code >= 200 && this.code < 300;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", code, description);
    }
}

/**
 * Cái Enum này là linh hồn của hệ thống, đừng có xóa bừa!
 */
//// Group 2xx: Success
//OK(200, "Thao tác thành công"),
//CREATED(201, "Đã tạo đối tượng thành công"),
//CONTINUE(202, "ID hợp lệ, tiếp tục nhập dữ liệu đi cu"), // Chính là cái mày cần
//
//// Group 4xx: Client err
//BAD_REQUEST(400, "Dữ liệu gửi lên như rác"),
//ID_INVALID(401, "ID không thỏa mãn điều kiện add_if_max"),
//ID_TAKEN(402, "ID ngon nhưng có thằng nhanh tay húp mất rồi"),
//NAME_INVALID(403, "Name không thỏa mãn điều kiện add_if_max"),
//FORMAT_INVALID (404, "FORMAT_INVALID: User nhập chữ vào ô số, nhập 'abc' vào ô boolean"),
//OUT_OF_RANGE (405, "OUT_OF_RANGE: số int, long bị quá to hoặc quá nhỏ"),
//REQUIRED_FIELD_MISSING (406, "REQUIRED_FIELD_MISSING: chỗ bắt buộc phải chọn true/false mà nó bỏ trống"),
//
//// Group 5xx: Server or System err
//SERVER_ERROR(500, "Server đang ngáo, thử lại sau"),
//SERVICE_UNAVAILABLE(503, "Server is unavailable at this time"),
//TIMEOUT(504, "Chờ mày nhập liệu lâu quá, tao đóng kết nối đây");