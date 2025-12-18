package com.asv.services.impl;

import com.asv.enums.ProcessState;
import com.asv.enums.ProcessType;
import com.asv.feign.HotelClientFeign;
import com.asv.models.bookingdto.BookingDTO;
import com.asv.models.bookingdto.BookingSimplDTO;
import com.asv.models.bookingdto.Guest;
import com.asv.models.roomdto.RoomDTO;
import com.asv.models.roomdto.RoomSimpleDataBaseDTO;
import com.asv.models.servicehoteldto.ServiceHotelDTO;
import com.asv.models.servicehoteldto.ServiceHotelSimpleDTO;
import com.asv.repositories.ChatEntityRepository;
import com.asv.services.AbstractProcessHandler;
import com.asv.services.BotSenderMessageService;
import com.asv.services.ProcessHandler;
import com.asv.sessions.UserSession;
import com.asv.ui.MenuButtonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static com.asv.services.StringText.*;

/**
 * Обработчик процесса создания нового бронирования.
 * Реализует многоэтапную логику:
 * <ol>
 *   <li>Ввод дат заезда и выезда с валидацией;</li>
 *   <li>Выбор номера комнаты с проверкой доступности на указанные даты;</li>
 *   <li>Указание количества проживающих (не более вместимости номера);</li>
 *   <li>Поочерёдный ввод данных для каждого проживающего (имя, фамилия, возраст, паспорт);</li>
 *   <li>Ввод промокода (опционально);</li>
 *   <li>Выбор дополнительных платных сервисов.</li>
 * </ol>
 * После сбора всех данных формируется и отправляется запрос на создание брони.
 * Конфиденциальные данные (паспорт, возраст) удаляются из чата после ввода.
 */
@Slf4j
@Service
public class BookingCreationProcessHandler extends AbstractProcessHandler implements ProcessHandler {
    private final HotelClientFeign hotelClientFeign;
    private final ChatEntityRepository chatEntityRepository;


    private static final DateTimeFormatter INPUT_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int LENGTH_LINE_FOR_SERVICES_LIMIT = 4;

    public BookingCreationProcessHandler(
            BotSenderMessageService sender,
            ChatEntityRepository chatEntityRepository,
            MenuButtonService menuButtonService,
            HotelClientFeign hotelClientFeign) {
        super(sender, menuButtonService);
        this.hotelClientFeign = hotelClientFeign;
        this.chatEntityRepository = chatEntityRepository;
    }

    /**
     * Проверяет, поддерживает ли данный обработчик указанный тип процесса.
     *
     * @param processType тип процесса
     * @return {@code true}, если тип процесса — {@link ProcessType#BOOKING}; {@code false} в противном случае
     */
    @Override
    public boolean canHandle(ProcessType processType) {
        return processType == ProcessType.BOOKING;
    }

    /**
     * Инициирует процесс бронирования: устанавливает начальное состояние сессии
     * и запрашивает у пользователя дату заезда.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя
     */
    @Override
    public void startProcess(long chatId, UserSession session) {
        session.startProcess(ProcessType.BOOKING);
        sendMessageWithBackButton(chatId, ENTER_BOOKING_CHECK_IN_DATES);
        log.info("Начат процесс бронирования для пользователя chatId  {}", chatId);
    }

    /**
     * Обрабатывает текущий шаг процесса бронирования в зависимости от состояния сессии.
     * Поддерживает 10 последовательных шагов ввода данных.
     * При неизвестном состоянии сессия сбрасывается, и отправляется сообщение об ошибке.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput ввод пользователя
     * @param session   сессия пользователя
     * @param messageId идентификатор входящего сообщения (используется для удаления конфиденциальных данных)
     */
    @Override
    public void handleStep(long chatId, String userInput, UserSession session, int messageId) {
        ProcessState currentState = session.getProcessState();
        switch (currentState) {
            case BOOKING_ENTERING_CHECK_IN_DATE -> handleCheckInDate(chatId, userInput, session);
            case BOOKING_ENTERING_CHECK_OUT_DATE -> handleCheckOutDate(chatId, userInput, session);
            case BOOKING_ENTERING_ROOM_NUMBER -> handleRoomNumber(chatId, userInput, session);
            case BOOKING_ENTERING_QUANTITY_PERSONS -> handleQuantityLivingPersons(chatId, userInput, session);
            case BOOKING_ENTERING_GUEST_NAME -> handleGuestNameGuest(chatId, userInput, session, messageId);
            case BOOKING_ENTERING_GUEST_SURNAME -> handleGuestSurameGuest(chatId, userInput, session, messageId);
            case BOOKING_ENTERING_GUEST_AGE -> handleAgeGuest(chatId, userInput, session, messageId);
            case BOOKING_ENTERING_GUEST_DOCUMENT -> handleDocumentNumber(chatId, userInput, session, messageId);
            case BOOKING_ENTERING_PROMO_CODE -> handlePromoCode(chatId, userInput, session);
            case BOOKING_ENTERING_SERVICES -> handleServices(chatId, userInput, session);
            default -> {
                log.warn("Неизвестное состояние для BOOKING: {}", currentState);
                session.resetProcessTypeAndState();
                sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, false);
            }
        }
    }

    /**
     * Обрабатывает ввод списка дополнительных сервисов.
     * Если пользователь вводит "нет" (или похожее короткое слово), сервисы не добавляются.
     * В противном случае выполняется параллельный запрос за списком всех сервисов
     * и фильтрация по введённым названиям.
     * После обработки запускается создание бронирования.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput строка с названиями сервисов через запятую или "нет"
     * @param session   сессия пользователя
     */
    private void handleServices(long chatId, String userInput, UserSession session) {
        if (userInput.length() <= LENGTH_LINE_FOR_SERVICES_LIMIT && userInput.toLowerCase().contains(WORD_NO)) {
            session.putData(SET_SERVICES_KEY, null);
            createRequestAndSendToHotelService(chatId, session);
            return;
        }
        Set<ServiceHotelSimpleDTO> serviceHotelSimpleDTOS = createServiceSet(userInput, chatId);
        session.putData(SET_SERVICES_KEY, serviceHotelSimpleDTOS);
        createRequestAndSendToHotelService(chatId, session);
    }

    /**
     * Формирует и отправляет запрос на создание бронирования в сервис отеля,
     * используя все собранные данные из сессии.
     * В случае успеха — отправляет пользователю подтверждение с деталями брони.
     * В случае ошибки — уведомляет о проблеме.
     * После завершения сессия сбрасывается.
     *
     * @param chatId  уникальный идентификатор чата в Telegram
     * @param session сессия пользователя с полными данными для бронирования
     */
    private void createRequestAndSendToHotelService(long chatId, UserSession session) {
        BookingSimplDTO bookingSimplDTO = BookingSimplDTO.builder()
                .checkInDate(session.getData(CHECK_IN_KEY, LocalDate.class))
                .checkOutDate(session.getData(CHECK_OUT_KEY, LocalDate.class))
                .persons(session.getData(QUANTITY_PERSONS_KEY, Integer.class))
                .promoCodeDTO(session.getData(PROMO_CODE_KEY, String.class))
                .guestList(session.getData(LIST_GUEST_KEY, List.class))
                .serviceSet(session.getData(SET_SERVICES_KEY, Set.class))
                .roomNumber(session.getData(ROOM_KEY, RoomDTO.class).getNumber())
                .build();
        log.info("Собраный запрос на бронирование {}", bookingSimplDTO);
        ResponseEntity<BookingDTO> response;
        try {
            response = hotelClientFeign.createBooking(bookingSimplDTO);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.info("Ошибка бронирования для chatId {} ", chatId);
                session.resetProcessTypeAndState();
                sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, isUserAuthenticated(chatId));
            }
            session.resetProcessTypeAndState();
            String bookingMessage = converBookingToMessage(response.getBody());
            sendMessageWithMenu(chatId, bookingMessage, isUserAuthenticated(chatId));

        } catch (Exception e) {
            session.resetProcessTypeAndState();
            log.info("Ошибка chatId {} {}", chatId, e.getMessage());
            sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, isUserAuthenticated(chatId));
        }
    }

    /**
     * Форматирует подтверждение бронирования в читаемое сообщение для пользователя.
     *
     * @param body объект с деталями созданного бронирования
     * @return отформатированная строка с ID брони, ценой, номером комнаты, гостями, промокодом и сервисами
     */
    private String converBookingToMessage(BookingDTO body) {
        String promoMassage;
        String servicesMassage;
        if (body.getPromoCodeDTO() == null || body.getPromoCodeDTO().isBlank()) {
            promoMassage = ABSENT;
        } else promoMassage = body.getPromoCodeDTO();
        if (body.getServiceHotelDTOS() == null || body.getServiceHotelDTOS().isEmpty()) {
            servicesMassage = ABSENT;
        } else {
            servicesMassage = body.getServiceHotelDTOS().stream()
                    .map(serviceHotelDTO -> serviceHotelDTO.getTitle())
                    .collect(Collectors.joining(", "));
        }


        return SUCCESSFUL + NEXT_LINE + BOOKING_N + body.getId() + NEXT_LINE + TOTAL_PRICE + DOUBLE_POINT + body.getTotalPrice() + SPACE + MONEY
                + NEXT_LINE + ROOM + body.getRoomSimpleDTO().getNumber() + NEXT_LINE + TYPE + body.getRoomSimpleDTO().getType()
                + NEXT_LINE + body.getRoomSimpleDTO().getDescription() + NEXT_LINE + LIST_OF_GUESTS + body.getGuestList().size() +
                NEXT_LINE + PROMO_CODE + promoMassage + NEXT_LINE + SERVICE + servicesMassage;

    }

    /**
     * Создаёт множество объектов {@link ServiceHotelSimpleDTO} на основе введённого пользователем списка названий.
     * Выполняет асинхронный запрос за полным списком сервисов и фильтрует по совпадению названий.
     *
     * @param userInput строка с названиями сервисов через запятую
     * @param chatId    уникальный идентификатор чата (для логирования)
     * @return множество DTO выбранных сервисов, или пустое множество в случае ошибки
     */
    private Set<ServiceHotelSimpleDTO> createServiceSet(String userInput, long chatId) {

        CompletableFuture<ResponseEntity<List<ServiceHotelDTO>>> responseServiceSetCompletableFuture = getAllServices();
        final Set<String> normalaizedServiceNames = normalaizedServiceNames(userInput);
        ResponseEntity<List<ServiceHotelDTO>> responseListServices;

        try {
            responseListServices = responseServiceSetCompletableFuture.join();
        } catch (CompletionException e) {
            log.warn("Error:Проблема с поиском сервисов для chatId {} ошбика {}", chatId, e.getMessage());
            return Collections.emptySet();
        }
        if (!responseListServices.getStatusCode().is2xxSuccessful()
                || responseListServices.getBody() == null
                || responseListServices.getBody().isEmpty()) {
            return Collections.emptySet();
        }

        return responseListServices.getBody().stream()
                .filter(serviceHotelDTO -> normalaizedServiceNames.contains(serviceHotelDTO.getTitle()))
                .map(serviceHotelDTO ->
                        ServiceHotelSimpleDTO.builder()
                                .title(serviceHotelDTO.getTitle())
                                .description(serviceHotelDTO.getDescription()).build())
                .collect(Collectors.toSet());

    }

    /**
     * Нормализует введённые пользователем названия сервисов: разбивает по запятым,
     * удаляет всё кроме букв и приводит к нижнему регистру.
     *
     * @param userInput строка с названиями сервисов через запятую
     * @return множество нормализованных названий
     */
    private Set<String> normalaizedServiceNames(String userInput) {
        String[] servicesArrayDirty = userInput.split(",");

        if (servicesArrayDirty == null) return null;

        Set<String> result = Arrays.stream(servicesArrayDirty)
                .filter(str -> str != null && !str.isBlank())
                .map(str -> str.replaceAll("[^\\p{L}]", "").toLowerCase())
                .collect(Collectors.toSet());
        return result;
    }

    /**
     * Асинхронно получает полный список доступных сервисов отеля.
     *
     * @return {@link CompletableFuture} с ответом от сервиса отеля
     */
    @Async
    public CompletableFuture<ResponseEntity<List<ServiceHotelDTO>>> getAllServices() {
        try {
            return CompletableFuture.completedFuture(hotelClientFeign.getAll());
        } catch (Exception e) {
            log.warn("Error: Не удалось получить данные всех возможных сервисов для бронирования");
            return CompletableFuture.completedFuture(null);
        }
    }

    /**
     * Обрабатывает ввод промокода и переходит к следующему шагу — выбору сервисов.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput введённый пользователем промокод
     * @param session   сессия пользователя
     */
    private void handlePromoCode(long chatId, String userInput, UserSession session) {
        session.saveDataAndContinue(PROMO_CODE_KEY, userInput.toUpperCase());
        sendMessageWithBackButton(chatId, ENTER_SERVICES);
    }

    /**
     * Обрабатывает ввод номера паспорта проживающего: удаляет сообщение с документом (для безопасности),
     * сохраняет данные и либо переходит к следующему гостю, либо завершает ввод гостей.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput номер паспорта
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с паспортом (удаляется)
     */
    private void handleDocumentNumber(long chatId, String userInput, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        List<Guest> listGuests = session.getData(LIST_GUEST_KEY, List.class);
        int counter = session.getData(COUNTER_KEY, Integer.class);
        int quantityP = session.getData(QUANTITY_PERSONS_KEY, Integer.class);
        Guest guest = listGuests.get(counter);
        guest.setNumberDocument(userInput);
        listGuests.set(counter, guest);

        if (counter + 1 < quantityP) {
            session.putData(LIST_GUEST_KEY, listGuests);
            counter++;
            session.setProcessState(ProcessState.BOOKING_ENTERING_GUEST_NAME);
            session.putData(COUNTER_KEY, counter);
            sendMessageWithBackButton(chatId, ENTER_NAME_LIVING_PERSONS);
            return;
        }
        session.saveDataAndContinue(LIST_GUEST_KEY, listGuests);
        sendMessageWithBackButton(chatId, ENTER_PROMO_CODE);

    }

    /**
     * Обрабатывает ввод возраста проживающего: удаляет сообщение с возрастом,
     * проверяет корректность (целое число от 1 до 127), сохраняет и переходит к вводу паспорта.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput строковое представление возраста
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с возрастом (удаляется)
     */
    private void handleAgeGuest(long chatId, String userInput, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        List<Guest> listGuests = session.getData(LIST_GUEST_KEY, List.class);
        int counter = session.getData(COUNTER_KEY, Integer.class);
        Guest guest = listGuests.get(counter);
        byte age = checkAndConvertAgeToByte(userInput);
        if (age < 0) {
            log.warn("Возраст не подходить для chatId {} age {}", chatId, age);
            session.resetProcessTypeAndState();
            sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA, isUserAuthenticated(chatId));
            return;
        }
        guest.setAge(age);
        listGuests.set(counter, guest);
        session.saveDataAndContinue(LIST_GUEST_KEY, listGuests);
        sendMessageWithBackButton(chatId, ENTER_DOCUMENT_LIVING_PERSONS);
    }

    /**
     * Преобразует строку в значение типа {@code byte} (возраст).
     * Возвращает -1 в случае ошибки.
     *
     * @param age строковое представление возраста
     * @return возраст как {@code byte}, или -1 при ошибке
     */
    private byte checkAndConvertAgeToByte(String age) {
        try {
            return Byte.valueOf(age);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Обрабатывает ввод фамилии проживающего: удаляет сообщение с фамилией,
     * сохраняет данные и переходит к вводу возраста.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput фамилия
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с фамилией (удаляется)
     */
    private void handleGuestSurameGuest(long chatId, String userInput, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        List<Guest> listGuests = session.getData(LIST_GUEST_KEY, List.class);
        int counter = session.getData(COUNTER_KEY, Integer.class);
        Guest guest = listGuests.get(counter);
        guest.setSurname(userInput);
        listGuests.set(counter, guest);
        session.saveDataAndContinue(LIST_GUEST_KEY, listGuests);
        log.info("Добавление фамилии для проживающего на позиции {} с фамилия {} chatId {} list size is {}", counter, userInput, chatId, listGuests.size());
        sendMessageWithBackButton(chatId, ENTER_AGE_LIVING_PERSONS);
    }

    /**
     * Обрабатывает ввод имени проживающего: удаляет сообщение с именем,
     * создаёт нового гостя, добавляет в список и переходит к вводу фамилии.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput имя
     * @param session   сессия пользователя
     * @param messageId идентификатор сообщения с именем (удаляется)
     */
    private void handleGuestNameGuest(long chatId, String userInput, UserSession session, int messageId) {
        deleteMessage(chatId, messageId);
        sendMessageWithBackButton(chatId, DATA_SUCCESSFUL_ACCEPT_AND_HIDE);
        Guest guest = new Guest();
        guest.setName(userInput);
        List<Guest> listGuests = findOrCreateListGuest(session, chatId);
        int counter = session.getData(COUNTER_KEY, Integer.class);
        listGuests.add(counter, guest);
        session.saveDataAndContinue(LIST_GUEST_KEY, listGuests);
        log.info("Добавление нового проживающего на позицию {} с именем {} chatId {} list size is {}", counter, userInput, chatId, listGuests.size());
        sendMessageWithBackButton(chatId, ENTER_SURNAME_LIVING_PERSONS);

    }

    /**
     * Возвращает существующий список гостей из сессии или создаёт новый,
     * если список отсутствует.
     *
     * @param session сессия пользователя
     * @param chatId  уникальный идентификатор чата (для логирования)
     * @return список гостей (возможно, пустой)
     */
    private List<Guest> findOrCreateListGuest(UserSession session, long chatId) {
        List<Guest> listGuests = session.getData(LIST_GUEST_KEY, List.class);
        if (listGuests == null || listGuests.isEmpty()) {
            int capacity = session.getData(QUANTITY_PERSONS_KEY, Integer.class);
            log.info("Создание нового списка проживающих для регистрации бронирования, для chatId {} list size is {}", chatId);
            return new ArrayList<>(capacity);
        }
        log.info("возврат списка проживающих для регистрации бронирования, для chatId {} list size is {}", chatId, listGuests.size());
        return listGuests;
    }

    /**
     * Обрабатывает ввод количества проживающих: проверяет, что число положительное
     * и не превышает вместимость выбранного номера.
     * При успехе инициирует ввод данных для первого гостя.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput строковое представление количества
     * @param session   сессия пользователя
     */
    private void handleQuantityLivingPersons(long chatId, String userInput, UserSession session) {
        String quantityP = userInput.strip();

        if (isCorrectQuantityPersonsForRoom(quantityP, session, chatId)) {
            int quatityPInt = Integer.valueOf(quantityP);
            session.saveDataAndContinue(QUANTITY_PERSONS_KEY, quatityPInt);
            session.putData(COUNTER_KEY, 0);
            sendMessageWithBackButton(chatId, ENTER_NAME_LIVING_PERSONS);
        } else {
            session.resetProcessTypeAndState();
            sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA, isUserAuthenticated(chatId));
            log.warn("метод  isCorrectQuantityPersonsForRoom вернул false для " +
                    "chatId {} ", chatId);
        }
    }

    /**
     * Проверяет корректность введённого количества проживающих:
     * должно быть целым положительным числом, не превышающим вместимость комнаты.
     *
     * @param quantityP строковое представление количества
     * @param session   сессия пользователя (содержит данные о комнате)
     * @param chatId    уникальный идентификатор чата (для логирования)
     * @return {@code true}, если количество корректно; {@code false} в противном случае
     */
    private boolean isCorrectQuantityPersonsForRoom(String quantityP, UserSession session, long chatId) {
        int roomCapacity = session.getData(ROOM_KEY, RoomDTO.class).getCapacity();
        try {
            int quantityPInt = Integer.parseInt(quantityP);
            return quantityPInt > 0 && quantityPInt <= roomCapacity;
        } catch (NumberFormatException e) {
            log.error("Корректность вводимых данных для " +
                    "chatId {} ,количество запрашиваемых мест {} в наличие {}", chatId, quantityP, roomCapacity);
            return false;
        }
    }

    /**
     * Обрабатывает ввод номера комнаты: параллельно получает информацию о комнате
     * и список свободных номеров на указанные даты, затем проверяет, доступен ли
     * выбранный номер. При успехе переходит к вводу количества проживающих.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param userInput номер комнаты
     * @param session   сессия пользователя
     */
    private void handleRoomNumber(long chatId, String userInput, UserSession session) {
        LocalDate checkInDate = session.getData(CHECK_IN_KEY, LocalDate.class);
        LocalDate checkOutDate = session.getData(CHECK_OUT_KEY, LocalDate.class);
        try {

            CompletableFuture<RoomDTO> roomDTOCompletableFuture = prepareRoomNumber(chatId, userInput);
            CompletableFuture<List<RoomSimpleDataBaseDTO>> listCompletableFuture = searchFreeRooms(checkInDate, checkOutDate, chatId);
            CompletableFuture<Void> allDone = CompletableFuture.allOf(roomDTOCompletableFuture, listCompletableFuture);
            allDone.join();

            RoomDTO roomDTO = roomDTOCompletableFuture.join();
            List<RoomSimpleDataBaseDTO> roomSimpleDataBaseDTOList = listCompletableFuture.join();

            if (isCorrectRoomForBooking(roomSimpleDataBaseDTOList, roomDTO)) {
                session.saveDataAndContinue(ROOM_KEY, roomDTO);
                sendMessageWithBackButton(chatId, ENTER_QUANTITY_LIVING_PERSONS);
                log.info("Даты для бронирования номера добавлены" +
                        "chatId {} ,даты {} - {} , room N {} ", chatId, checkInDate, checkOutDate, userInput);
            } else {
                log.error("Неверные даты для бронирования номера" +
                        "chatId {} ,даты {} - {} , room N {} ", chatId, checkInDate, checkOutDate, userInput);
                sendMessageWithMenu(chatId, PLEASE_ENTER_WRIGHT_DATA + NOT_AVAILABLE + AVAILABLE_ROOMS, isUserAuthenticated(chatId));
                session.resetProcessTypeAndState();
            }

        } catch (CompletionException ex) {
            log.error("Проблемы с получением данных для бронирования на этапе BOOKING_ENTERING_ROOM_NUMBER для чата" +
                    "chatId {} ,даты {} - {} , room N {} ", chatId, checkInDate, checkOutDate, userInput);
            sendMessageWithMenu(chatId, PROBLEM_TRY_AGAIN_LATER, isUserAuthenticated(chatId));
            session.resetProcessTypeAndState();
        }

    }

    /**
     * Проверяет, входит ли указанный номер комнаты в список свободных на заданный период.
     *
     * @param roomSimpleDataBaseDTOList список DTO свободных комнат
     * @param roomDTO                   DTO запрошенной комнаты
     * @return {@code true}, если комната доступна; {@code false} в противном случае
     */
    private boolean isCorrectRoomForBooking(List<RoomSimpleDataBaseDTO> roomSimpleDataBaseDTOList, RoomDTO roomDTO) {
        if (roomDTO == null) return false;
        final String requestRoom = roomDTO.getNumber();
        return roomSimpleDataBaseDTOList.stream()
                .map(roomSimpleDataBaseDTO -> roomSimpleDataBaseDTO.getNumber())
                .anyMatch(roomNumber -> roomNumber.equals(requestRoom));
    }

    /**
     * Асинхронно получает информацию о комнате по её номеру.
     *
     * @param chatId     уникальный идентификатор чата (для логирования)
     * @param roomNumber номер комнаты
     * @return {@link CompletableFuture} с DTO комнаты или {@code null}, если комната не найдена
     */
    @Async
    public CompletableFuture<RoomDTO> prepareRoomNumber(long chatId, String roomNumber) {
        String normalizedRoomNumber = roomNumber.strip();

        if (normalizedRoomNumber.isBlank()) {
            log.info("Запрос информации для пустой комнаты отправлена пользователю chatId, {}", chatId);
            return CompletableFuture.completedFuture(null);
        }

        try {
            ResponseEntity<RoomDTO> response = hotelClientFeign.getRoomByNumber(normalizedRoomNumber);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("Информация о комнате {} отправлена пользователю chatId, {}", roomNumber, chatId);
                return CompletableFuture.completedFuture(response.getBody());
            } else {
                log.warn("Комната {} не найдена для пользователя chatId, {}", normalizedRoomNumber, chatId);
                return CompletableFuture.completedFuture(null);
            }

        } catch (Exception e) {
            log.error("Ошибка при получении информации о комнате {} для пользователя chatId, {}", normalizedRoomNumber, chatId, e);
            return CompletableFuture.completedFuture(null);
        }

    }

    /**
     * Обрабатывает введённую пользователем дату заезда:
     * парсит, проверяет формат и логическую валидность (не ранее текущей даты).
     * При успехе сохраняет дату и запрашивает дату выезда.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param dateInput строка с датой заезда в формате "ДД.ММ.ГГГГ"
     * @param session   сессия пользователя
     */
    private void handleCheckInDate(long chatId, String dateInput, UserSession session) {
        Optional<LocalDate> parsedDate = parseDate(dateInput);
        log.info(parsedDate.toString());
        if (parsedDate.isEmpty()) {
            sendMessageWithBackButton(chatId, INCORRECT_DATE_FORMATE);
            return;
        }

        LocalDate checkInDate = parsedDate.get();
        LocalDate today = LocalDate.now();

        if (checkInDate.isBefore(today)) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }

        session.saveDataAndContinue(CHECK_IN_KEY, checkInDate);
        sendMessageWithBackButton(chatId, ENTER_BOOKING_CHECK_OUT_DATES);
        log.debug("Пользователь {} ввел дату заезда: {}", chatId, checkInDate.format(INPUT_DATE_FORMAT));
    }

    /**
     * Обрабатывает введённую пользователем дату выезда:
     * парсит, проверяет формат и логическую валидность (строго после даты заезда и не сегодня/в прошлом).
     * При успехе сохраняет дату и запрашивает номер комнаты.
     *
     * @param chatId    уникальный идентификатор чата в Telegram
     * @param dateInput строка с датой выезда в формате "ДД.ММ.ГГГГ"
     * @param session   сессия пользователя
     */
    private void handleCheckOutDate(long chatId, String dateInput, UserSession session) {
        Optional<LocalDate> parsedDate = parseDate(dateInput);

        if (parsedDate.isEmpty()) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }
        LocalDate checkOutDate = parsedDate.get();
        LocalDate checkInDate = session.getData(CHECK_IN_KEY, LocalDate.class);
        LocalDate today = LocalDate.now();

        if (checkOutDate.isBefore(today) || checkOutDate.isEqual(today)) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            sendMessageWithBackButton(chatId, PLEASE_ENTER_WRIGHT_DATA_DATES);
            return;
        }

        session.saveDataAndContinue(CHECK_OUT_KEY, checkOutDate);
        sendMessageWithBackButton(chatId, ENTER_ROOM_N);
    }

    /**
     * Парсит строку даты в формате "ДД.ММ.ГГГГ" в объект {@link LocalDate}.
     *
     * @param dateStr строка с датой
     * @return {@link Optional} с распарсенной датой, если парсинг успешен; пустой {@link Optional} в случае ошибки
     */
    private Optional<LocalDate> parseDate(String dateStr) {
        try {
            dateStr = dateStr.strip();
            LocalDate date = LocalDate.parse(dateStr, INPUT_DATE_FORMAT);
            return Optional.of(date);
        } catch (DateTimeParseException e) {
            log.debug("Ошибка парсинга даты: {}", dateStr, e);
            return Optional.empty();
        }
    }

    /**
     * Проверяет, аутентифицирован ли пользователь по наличию JWT-токена, привязанного к chatId.
     *
     * @param chatId уникальный идентификатор чата в Telegram
     * @return {@code true}, если токен найден; {@code false} в противном случае
     */
    private boolean isUserAuthenticated(long chatId) {

        return chatEntityRepository.findTokenByChatId(chatId).isPresent();
    }

    /**
     * Асинхронно выполняет поиск свободных номеров отеля на заданный период.
     *
     * @param checkInDate  дата заезда
     * @param checkOutDate дата выезда
     * @param chatId       уникальный идентификатор чата (для логирования)
     * @return {@link CompletableFuture} со списком DTO свободных номеров
     */
    @Async
    public CompletableFuture<List<RoomSimpleDataBaseDTO>> searchFreeRooms(LocalDate checkInDate, LocalDate checkOutDate, long chatId) {
        log.info("Поиск свободных комнат с {} по {} для пользователя {}",
                checkInDate, checkOutDate, chatId);
        String checkInStr = checkInDate.toString();
        String checkOutStr = checkOutDate.toString();
        try {
            ResponseEntity<List<RoomSimpleDataBaseDTO>> response =
                    hotelClientFeign.getFreeRoomsBetweenDates(checkInStr, checkOutStr);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<RoomSimpleDataBaseDTO> freeRooms = response.getBody();

                if (freeRooms.isEmpty()) {
                    log.info("Свободных комнат не найдено для пользователя chatId {}", chatId);
                    return CompletableFuture.completedFuture(Collections.emptyList());
                } else {
                    log.info("Найдено {} свободных комнат для пользователя {}", freeRooms.size(), chatId);
                    return CompletableFuture.completedFuture(freeRooms);
                }

            } else {
                log.warn("Ошибка ответа от сервера при поиске свободных комнат для пользователя chatId {}", chatId);
                return CompletableFuture.completedFuture(Collections.emptyList());
            }
        } catch (Exception e) {
            log.error("Ошибка при поиске свободных комнат для пользователя chatId {}", chatId, e);
            return CompletableFuture.completedFuture(Collections.emptyList());
        }
    }
}
