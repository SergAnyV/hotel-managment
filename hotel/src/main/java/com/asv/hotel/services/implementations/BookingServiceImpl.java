package com.asv.hotel.services.implementations;

import com.asv.hotel.dto.bookingdto.BookingDTO;
import com.asv.hotel.dto.bookingdto.BookingSimplDTO;
import com.asv.hotel.dto.bookingdto.ResponseBookingDTO;
import com.asv.hotel.dto.mapper.BookingMapper;
import com.asv.hotel.dto.mapper.ServiceHotelMapper;
import com.asv.hotel.dto.roomdto.RoomSimpleDataBaseDTO;
import com.asv.hotel.dto.servicehoteldto.ServiceHotelSimpleDTO;
import com.asv.hotel.entities.*;
import com.asv.hotel.entities.enums.BookingStatus;
import com.asv.hotel.entities.enums.UserRole;
import com.asv.hotel.exceptions.HotelDataNotFoundException;
import com.asv.hotel.exceptions.HotelIncorrectInputData;
import com.asv.hotel.repositories.BookingRepository;
import com.asv.hotel.security.util.JWTUtils;
import com.asv.hotel.services.*;
import com.asv.hotel.util.BookingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
/**
 * Реализация сервиса управления бронированиями.
 * <p>
 * Обеспечивает:
 * <ul>
 *   <li>создание бронирования с расчётом стоимости (с учётом промокодов и доп. услуг);</li>
 *   <li>удаление бронирования (с учётом прав доступа: пользователь, менеджер, админ);</li>
 *   <li>поиск бронирований по номеру комнаты;</li>
 *   <li>поиск свободных комнат на заданные даты;</li>
 *   <li>получение детальной информации о бронировании (с учётом прав доступа).</li>
 * </ul>
 * </p>
 * <p>
 * При создании бронирования:
 * <ul>
 *   <li>проверяется доступность номера на указанные даты;</li>
 *   <li>валидируется количество гостей против вместимости номера;</li>
 *   <li>применяется активный промокод (если указан);</li>
 *   <li>рассчитывается итоговая стоимость;</li>
 *   <li>создаётся уведомление о бронировании.</li>
 * </ul>
 * </p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserInternalService userService;
    private final RoomInternalService roomInternalService;
    private final ServiceHotelInternalService serviceHotelInternalService;
    private final PromoCodeInternalService promoCodeInternalService;
    private final NotificationHotelService notificationHotelService;

    /**
     * Создаёт новое бронирование на основе упрощённых данных.
     * <p>
     * Извлекает текущего пользователя из контекста безопасности.
     * Проверяет доступность номера, вместимость, применяет промокод и рассчитывает итоговую стоимость.
     * </p>
     *
     * @param bookingSimplDTO данные для создания бронирования
     * @return DTO с полной информацией о созданном бронировании
     * @throws HotelIncorrectInputData если данные некорректны (например, гостей больше вместимости)
     * @throws HotelDataNotFoundException если номер или промокод не найдены
     */
    @Transactional
    public BookingDTO createBooking(BookingSimplDTO bookingSimplDTO) {
        Booking booking = BookingMapper.INSTANCE.bookingSimpleDTOToBooking(bookingSimplDTO);
        //    поиск и установление комнаты для бронирования
        Room room = findRoomForBooking(bookingSimplDTO);
        booking.setRoom(room);

        if (booking.getGuestList().size() > room.getCapacity()) {
            log.warn("Warning: количество гостей при бронирование превышает возможности номера {}",
                    bookingSimplDTO.getRoomNumber());
            throw new HotelIncorrectInputData(String.format("Неверное количество гостей '%s' при бронирование комнаты '%s'",
                    booking.getGuestList().size(),
                    bookingSimplDTO.getRoomNumber()));
        }
        //    поиск и установление юзера из базы данных для бронирования
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().
                getAuthentication().
                getPrincipal();
        String nickname = userDetails.getUsername();
        User user = userService.findUserByNickName(nickname);
        booking.setUser(user);

        //поиск и установление промокода
        PromoCode promoCode = promoCodeInternalService.findActivePromoCodeByName(bookingSimplDTO.getPromoCodeDTO());
        booking.setPromoCode(promoCode);
        //    поиск и установление сервисов для бронирования
        Set<ServiceHotel> serviceHotel = findAllServicesForBooking(bookingSimplDTO);
        booking.setServiceSet(serviceHotel);
        //    расчет количества дней проживания и стоимости сервисов для этого периода
        BigDecimal livingDays = BookingUtils.calculateLivingDays(bookingSimplDTO.getCheckInDate(), bookingSimplDTO.getCheckOutDate());
        BigDecimal totalPriceForServices = calculatePriceForServices(serviceHotel, livingDays);
        //     расчет стоимости за номер c сервисами без учета промокода
        BigDecimal totalPrice = calculateTtalPriceWithoutPromoCode(room, livingDays, totalPriceForServices);
        //     расчет стоимости за номер c сервисами c учетом промокода
        totalPrice = calculatePriceWithPromoCode(bookingSimplDTO, totalPrice);
        booking.setTotalPrice(totalPrice);
        booking.setStatusOfBooking(BookingStatus.CONFIRMED);
        Booking savedBooking = bookingRepository.save(booking);
        notificationHotelService.createNotificationBooking("Номер забронирован", savedBooking, "Бронирование номера");
        return BookingMapper.INSTANCE.bookingToBookingDTO(savedBooking);
    }

    /**
     * Удаляет бронирование по ID с учётом прав доступа:
     * <ul>
     *   <li>Администратор и менеджер — могут удалить любое бронирование;</li>
     *   <li>Обычный пользователь — только своё.</li>
     * </ul>
     * <p>
     * Перед удалением очищаются связи с гостями и доп. услугами.
     * </p>
     *
     * @param id идентификатор бронирования
     * @throws HotelDataNotFoundException если бронирование не найдено или недоступно для удаления
     */
    @Transactional
    public void deleteBookingById(Long id) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserRole userRole = user.getType().getRole();
        Booking booking = null;
        if (userRole.equals(UserRole.ADMIN) || userRole.equals(UserRole.MANAGER)) {
            booking = bookingRepository.findById(id).orElseThrow(() -> new HotelDataNotFoundException(
                    String.format("Бронирование с номером %d для удаления не найдено", id)));
            booking.getServiceSet().clear();
            booking.getGuestList().clear();
            bookingRepository.delete(booking);
        } else {
            booking = bookingRepository.findBookingByIDAndUser_NickName(user.getNickName(), id).orElseThrow(() -> new HotelDataNotFoundException(
                    String.format("Бронирование с номером %d для удаления не найдено", id)));
            booking.getServiceSet().clear();
            booking.getGuestList().clear();
            bookingRepository.delete(booking);
        }
    }

    /**
     * Возвращает все бронирования, связанные с указанным номером комнаты.
     *
     * @param roomNumber текстовый номер комнаты (например: "101", "ЛЮКС-3")
     * @return список упрощённых DTO бронирований
     * @throws HotelDataNotFoundException если бронирования не найдены
     */
    @Transactional
    public List<BookingSimplDTO> findAllBookingsSimpleDTOByRoomNumber(String roomNumber) {
        List<Booking> bookingsList = bookingRepository.findAllByRoomNumber(roomNumber);
        if (bookingsList.isEmpty()) {
            log.error("лист с бронированиями пуст для данной комнаты {}", roomNumber);
            throw new HotelDataNotFoundException("лист с бронированиями пуст для данной комнаты");
        }
        return bookingsList.stream().map(b ->
                        BookingMapper.INSTANCE.bookingToBookingSimpleDTO(b))
                .toList();
    }

    /**
     * Возвращает список свободных номеров на заданный период.
     * <p>
     * Проверяет корректность дат: дата выезда должна быть строго после даты заезда.
     * </p>
     *
     * @param checkInDate  дата заезда
     * @param checkOutDate дата выезда
     * @return список DTO свободных номеров
     * @throws HotelIncorrectInputData если даты указаны некорректно
     */
    @Transactional
    @Override
    public List<RoomSimpleDataBaseDTO> findRoomSimpleDataBaseDTOByBookingDate(LocalDate checkInDate, LocalDate checkOutDate) {
        if (!checkInDate.isBefore(checkOutDate)) {
            log.error("Error:некорректные данные для поиска бронирования по датам заселение {} выселение {}",
                    checkInDate, checkOutDate);
            throw new HotelIncorrectInputData("Booking ", " Checking and Checkout dates");
        }

        return bookingRepository.findAllFreeRoomsBetweenDates(checkInDate, checkOutDate);
    }

    /**
     * Возвращает детальную информацию о бронировании по ID с учётом прав доступа:
     * <ul>
     *   <li>Администратор и менеджер — видят любое бронирование;</li>
     *   <li>Обычный пользователь — только своё.</li>
     * </ul>
     *
     * @param id идентификатор бронирования
     * @return DTO с полной информацией о бронировании
     * @throws HotelDataNotFoundException если бронирование не найдено или недоступно
     */
    @Transactional
    public ResponseBookingDTO findResponseBookingDTOByBookingId(Long id) {

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserRole userRole = user.getType().getRole();
        Booking booking = null;
        if (userRole.equals(UserRole.ADMIN) || userRole.equals(UserRole.MANAGER)) {
            booking = bookingRepository.findById(id).orElse(null);
            return getResponseBookingDTO(booking, id);
        } else {
            booking = bookingRepository.findBookingByIDAndUser_NickName(user.getNickName(), id).orElse(null);
            return getResponseBookingDTO(booking, id);
        }

    }

    // === Приватные вспомогательные методы ===

    /**
     * Формирует DTO с полной информацией о бронировании.
     *
     * @param booking бронирование
     * @param id      идентификатор (для сообщения об ошибке, если booking == null)
     * @return DTO с данными о бронировании, пользователе, номере и услугах
     * @throws HotelDataNotFoundException если booking == null
     */

    private ResponseBookingDTO getResponseBookingDTO(Booking booking, Long id) {
        if (booking == null) {
            log.warn("Error: неверный номер брониования в методе findBesponseBookingDTOByBookingId id= {}", id);
            throw new HotelDataNotFoundException("нет такого номера бронирования");
        }

        Set<ServiceHotelSimpleDTO> serviceHotelSimpleDTOS = booking.getServiceSet().stream()
                .map(serviceHotel ->
                        ServiceHotelMapper.INSTANCE.serviceHotelToServiceHotelSimpleDTO(serviceHotel))
                .collect(Collectors.toSet());

        return ResponseBookingDTO.builder()
                .bookingId(booking.getId())
                .statusOfBooking(booking.getStatusOfBooking())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .persons(booking.getPersons())
                .totalPrice(booking.getTotalPrice())
                .roomNumber(booking.getRoom().getNumber())
                .type(booking.getRoom().getType())
                .descriptionTypeOfRoom(booking.getRoom().getType().getDescription())
                .firstName(booking.getUser().getFirstName())
                .lastName(booking.getUser().getLastName())
                .email(booking.getUser().getEmail())
                .phoneNumber(booking.getUser().getPhoneNumber())
                .serviceHotelSimpleDTOS(serviceHotelSimpleDTOS)
                .guestList(booking.getGuestList())
                .build();
    }

    /**
     * Находит все дополнительные сервисы, указанные в запросе на бронирование.
     *
     * @param bookingSimplDTO исходный DTO с перечнем услуг
     * @return множество сущностей {@link ServiceHotel}
     */
    private Set<ServiceHotel> findAllServicesForBooking(BookingSimplDTO bookingSimplDTO) {
        Set<ServiceHotelSimpleDTO> serviceHotelDTOS = bookingSimplDTO.getServiceSet();
        if (serviceHotelDTOS==null||serviceHotelDTOS.isEmpty()) {
            return Collections.emptySet();
        }
        return serviceHotelDTOS.stream().map(serviceHotelSimpleDTO -> {
            return serviceHotelInternalService.findServiceHotelByTitle(serviceHotelSimpleDTO.getTitle());
        }).collect(Collectors.toSet());
    }

    /**
     * Рассчитывает итоговую стоимость бронирования с учётом промокода.
     * <p>
     * Поддерживает два типа скидок:
     * <ul>
     *   <li>{@code FIXED} — фиксированная сумма;</li>
     *   <li>{@code PERCENT} — процент (ошибка в текущей реализации: расчёт некорректен).</li>
     * </ul>
     * </p>
     *
     * @param totalPrice итоговая стоимость без промокода
     * @param promoCode  промокод (может быть null)
     * @return стоимость с учётом скидки
     */
    private BigDecimal calculateTotalPriceWithPromoCode(BigDecimal totalPrice, PromoCode promoCode) {
        if (promoCode == null) {
            return totalPrice;
        }
        return switch (promoCode.getTypeOfPromoCode()) {
            case FIXED -> totalPrice.subtract(promoCode.getDiscountValue());
            case PERCENT -> totalPrice.multiply(BigDecimal.valueOf(100)
                    .divide(promoCode.getDiscountValue(),
                            2,
                            RoundingMode.HALF_UP));
        };
    }

    /**
     * Проверяет доступность номера на указанные даты.
     *
     * @param bookingSimplDTO данные бронирования
     * @return сущность {@link Room}, если номер доступен
     * @throws HotelDataNotFoundException если номер не найден или занят
     */
    private Room findRoomForBooking(BookingSimplDTO bookingSimplDTO) {
        Room room = roomInternalService.findRoomByNumber(bookingSimplDTO.getRoomNumber());
        if (room == null || (Boolean.TRUE.equals(room.getIsAvailable()) && !bookingRepository.isRoomAvailableForDates(room.getId(),
                bookingSimplDTO.getCheckInDate(), bookingSimplDTO.getCheckOutDate()))) {
            throw new HotelDataNotFoundException("комната не свободна на данные даты или нет такой комнаты ") {
            };
        }

        return room;
    }

    /**
     * Рассчитывает стоимость дополнительных услуг за период проживания.
     * @return суммарная стоимость услуг
     */
    private User findUserForBooking(BookingSimplDTO bookingSimplDTO) {
        User user = userService.findUserByLastNameAndFirstName(
                bookingSimplDTO.getUserSimpleDTO().getLastName(), bookingSimplDTO.getUserSimpleDTO().getFirstName());
        return user;
    }

    /**
     * Рассчитывает базовую стоимость (номер + услуги) без учёта промокода.
     *
     * @param livingDays             количество дней проживания
     * @return общая стоимость без скидки
     */
    private BigDecimal calculatePriceForServices(Set<ServiceHotel> serviceHotels, BigDecimal livingDays) {
        if (!serviceHotels.isEmpty()) {
            return serviceHotels.stream().map(serviceHotelentity -> {
                        if (serviceHotelentity == null) {
                            return BigDecimal.ZERO;
                        }
                        return serviceHotelentity.getPrice().multiply(livingDays);
                    })
                    .reduce(BigDecimal.ZERO, (sum, price) -> sum.add(price));
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Применяет промокод к итоговой стоимости.
     * @return стоимость с учётом промокода
     */
    private BigDecimal calculateTtalPriceWithoutPromoCode(Room room, BigDecimal livingDays, BigDecimal totalPriceForServices) {
        return room.getPricePerNight()
                .multiply(livingDays).add(totalPriceForServices);

    }

    private BigDecimal calculatePriceWithPromoCode(BookingSimplDTO bookingSimplDTO, BigDecimal totalPrice) {
        PromoCode promoCode = promoCodeInternalService.findActivePromoCodeByName(bookingSimplDTO.getPromoCodeDTO());
        return calculateTotalPriceWithPromoCode(totalPrice, promoCode);
    }

}
