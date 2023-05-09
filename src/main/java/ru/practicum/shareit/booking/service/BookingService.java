package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItem;

import java.util.List;

public interface BookingService {
    /**
     * СОздание нового бронирования
     *
     * @param newBookingDto - новое бронирование в виде DTO
     * @return - новое бронирование, взятое из хранилища в виде DTO
     */
    BookingDtoWithItem createBooking(BookingDto newBookingDto, int bookerId);

    /**
     * Получение DTO бронирования из хранилища по id
     *
     * @param bookingId - id нужного брониврования
     * @param userId    - id пользователя. Должен быть владельцем вещи или автором брониварония
     * @return DTO бронироания
     */
    BookingDtoWithItem getBookingDtoById(int bookingId, int userId);

    /**
     * Возвращение списка всех бронирований пользователя
     *
     * @return Список бронирований конкретного пользователя
     */
    List<BookingDtoWithItem> getAllDtoByUserAndState(int userId, String state);

    /**
     * Получение списка бронирований для всех вещей текущего пользователя
     * @param userId - id владельца
     * @param state - статус бронирований
     * @return - список бронирований
     */
    List<BookingDtoWithItem> getAllDtoByOwnerAndState(int userId, String state);

    /**
     * Удаление бронирования из хранилища
     *
     * @param id - номер бронирования
     */
    void deleteBooking(int id);

    /**
     * Обновление Бронирования в хранилище
     *
     * @param bookingId - номер бронирования
     * @param approved  - подтверждение бронивания
     * @param bookerId  - id пользователя, сделавшего бронирование
     * @return - DTO обновленного бронирования
     */
    BookingDtoWithItem updateBooking(int bookingId, boolean approved, int bookerId);

}
