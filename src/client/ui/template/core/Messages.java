package client.ui.template.core;

import java.util.Locale;

public final class Messages {
    private Messages() {}

    public enum Lang {
        RU("ru", "RU"),
        EN_CA("en", "CA"),
        VI("vi", "VN");

        private final Locale locale;

        Lang(String language, String country) {
            this.locale = new Locale(language, country);
        }

        public Locale locale() {
            return locale;
        }
    }

    private record Translation(String ru, String en, String vi) {
        String get(Lang lang) {
            return switch (lang) {
                case RU -> ru;
                case EN_CA -> en;
                case VI -> vi;
            };
        }
    }

    public enum Key {
        LOGIN_TITLE(new Translation("Авторизация", "Login", "Đăng nhập")),
        LOGIN(new Translation("Войти", "Login", "Đăng nhập")),
        REGISTER(new Translation("Зарегистрироваться", "Register", "Đăng ký")),
        USERNAME(new Translation("Логин", "Username", "Tên đăng nhập")),
        PASSWORD(new Translation("Пароль", "Password", "Mật khẩu")),
        CURRENT_USER(new Translation("Текущий пользователь", "Current user", "Người dùng hiện tại")),
        ADD(new Translation("Добавить", "Add", "Thêm")),
        EDIT(new Translation("Изменить", "Edit", "Sửa")),
        DELETE(new Translation("Удалить", "Delete", "Xóa")),
        REFRESH(new Translation("Обновить", "Refresh", "Cập nhật")),
        LOGOUT(new Translation("Выйти", "Logout", "Đăng xuất")),
        SAVE(new Translation("Сохранить", "Save", "Lưu")),
        CANCEL(new Translation("Отмена", "Cancel", "Hủy")),
        EMPTY_TITLE(new Translation("Нет данных", "No data", "Không có dữ liệu")),
        EMPTY_DESCRIPTION(new Translation("Коллекция пока пустая", "The collection is empty", "Collection hiện đang trống"));

        private final Translation translation;

        Key(Translation translation) {
            this.translation = translation;
        }
    }

    private static Lang currentLang = Lang.RU;

    public static void setLang(Lang lang) {
        currentLang = lang;
    }

    public static Lang getCurrentLang() {
        return currentLang;
    }

    public static String get(Key key) {
        return key.translation.get(currentLang);
    }
}
