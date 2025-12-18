package com.asv.services;

/**
 * Утилитный класс, содержащий константы строковых литералов,
 * используемых в бизнес-логике и взаимодействии с пользователем.
 * Разделён на две логические группы:
 * <ul>
 *   <li>Сообщения, отображаемые пользователю (тексты интерфейса, ошибки, подсказки)</li>
 *   <li>Ключи для хранения данных во временных сессиях (используются в {@link com.asv.sessions.UserSession})</li>
 * </ul>
 * Все поля объявлены как {@code public static final} и не предназначены для изменения.
 */
public final class StringText {

    public static final String START_BOT = "/start";
    public static final String MAIN_MENU = "Главное меню:";
    public static final String ROOM = "Комната ";
    public static final String TYPE = "Тип ";
    public static final String DOUBLE_POINT = ": ";
    public static final String NEXT_LINE = "\n";
    public static final String PRICE = "Цена ";
    public static final String MONEY = " p.";
    public static final String MAX_CAPACITY_LIVING = "Максимально число проживающих ";
    public static final String SERVICE = "Сервис ";
    public static final String BOOKING_N = "Бронь № ";
    public static final String CHECK_IN_UP = "Заезд: ";
    public static final String CHECK_OUT_UP = "Выезд: ";
    public static final String BOOKING_N_NOT_FOUND = "Бронирование не найдено.Необходимо вводить номер реального бронирования";
    public static final String PLEASE_ENTER_WRIGHT_DATA = "Введите корректные данные. ";
    public static final String BOOKING_REMOVED = "Бронирование удалено.";
    public static final String CANT_BOOKING_REMOVED = "Не удалось удалить бронирование.";
    public static final String DATA_SUCCESSFUL_ACCEPT_AND_HIDE = "Данные приняты и скрыты для безопасности.";
    public static final String PLEASE_USE_MENU = "Пожалуйста, используйте кнопки меню:";
    public static final String INCORRECT_DATE_FORMATE = "Неверный формат даты. Используйте ДД.ММ.ГГГГ:";
    public static final String PLEASE_ENTER_WRIGHT_DATA_DATES = "Введите корректные данные. Дата заезда должна быть сегодня или позже, дата выезда должна быть после даты заселения";
    public static final String CLIENT = "клиент";
    public static final String REGISTRATION_SUCCESSFUL = "Регистрация успешна! Подтвердите регистрацию пройдя по ссылке в отправленном вам письме и можете входить";
    public static final String PROBLEM_TRY_AGAIN_LATER = "Произошла ошибка. Попробуйте снова позже.";
    public static final String SERVER_IS_NOT_AVAILABLE = "Ошибка регистрации.Проверьте вводимые вами данные и повторите.";
    public static final String NOT_FOUND = " не найдена.";
    public static final String NOT_FOUND_FREE_ROOMS = "Свободных комнат на указанные даты не найдено.";
    public static final String ACCESSIBLE_ROOM = "Доступные комнаты ";
    public static final String SPACE = " ";
    public static final String TOTAL_PRICE = "Полная стоимость: ";
    public static final String STATUS = "Статус ";
    public static final String CONTACT_PHONE_NUMBER = "Контактный номер телефона ";
    public static final String SERVICES = "Сервисы: ";
    public static final String INFORMATION = "Информация ";
    public static final String NAME = "Имя ";
    public static final String LIST_OF_GUESTS = "Список гостей :";
    public static final String SURNAME = "Фамилия ";
    public static final String SUCCESSFUL = "Успешно! ";
    public static final String WORD_NO = "нет";
    public static final String PROMO_CODE = "Промокод ";
    public static final String ABSENT = "отсутствует";
    public static final String NOT_AVAILABLE = "Ваш выбор не доступен.";
    public static final String AVAILABLE_ROOMS = "Доступные комнаты для демонстрационного отеля 101,201,301,401,102,202,302,402,203,103";


    public static final String NICKNAME_KEY = "nickName";
    public static final String NAME_KEY = "name";
    public static final String FATHERS_NAME_KEY = "fathersName";
    public static final String FAMILY_KEY = "lastName";
    public static final String EMAIL_KEY = "email";
    public static final String PHONE_NUMBER_KEY = "phoneNumber";
    public static final String CHECK_IN_KEY = "checkIn";
    public static final String CHECK_OUT_KEY = "checkOut";
    public static final String ROOM_KEY = "room";
    public static final String QUANTITY_PERSONS_KEY = "quantityPersons";
    public static final String COUNTER_KEY = "counter";
    public static final String LIST_GUEST_KEY = "listGuest";
    public static final String PROMO_CODE_KEY = "promoCode";
    public static final String SET_SERVICES_KEY = "serviceList";

    public static final String ENTER_NAME = "Имя может содержать только русские буквы, дефисы.\nВведите имя:";
    public static final String ENTER_NICKNAME = "Введите никнейм:";
    public static final String ENTER_FATHER_NAME = "Отчество может содержать только русские буквы, дефисы.\nВведите отчество";
    public static final String ENTER_FAMILY_NAME = "Фамилия может содержать только русские буквы, дефисы.\nВведите фамилию:";
    public static final String ENTER_EMAIL = "Введите email:";
    public static final String ENTER_PHONE_NUMBER = "Введите номер телефона в формате 89001234567";
    public static final String ENTER_BOOKING_CHECK_IN_DATES = "Введите дату заезда в формате ДД.ММ.ГГГГ .\nПример : 02.04.2026";
    public static final String ENTER_BOOKING_CHECK_OUT_DATES = "Введите дату выезда в формате ДД.ММ.ГГГГ .\nПример : 04.04.2026";
    public static final String ENTER_PASSWORD = "Введите пароль:";
    public static final String ENTER_ROOM_N = "Введите номер комнаты:";
    public static final String ENTER_SERVICE_NAME = "Введите название сервиса:";
    public static final String ENTER_BOOKING_NUMBER = "Введите номер бронирования";
    public static final String ENTER_QUANTITY_LIVING_PERSONS = "Введите количество проживающих";
    public static final String ENTER_NAME_LIVING_PERSONS = "Введите имя проживающего";
    public static final String ENTER_SURNAME_LIVING_PERSONS = "Введите фамилию проживающего";
    public static final String ENTER_AGE_LIVING_PERSONS = "Введите возраст проживающего";
    public static final String ENTER_DOCUMENT_LIVING_PERSONS = "Введите номер паспорта проживающего";
    public static final String ENTER_PROMO_CODE = "Введите промокод, если знаете, в противном случае введите 'НЕТ'";
    public static final String ENTER_SERVICES = "Введите название сервисов через запятую ','  , если вам не нужны дополнительные платные сервисы просто напишите 'НЕТ'";

}
