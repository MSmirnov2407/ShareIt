package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItem;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * СОздание брониварования
     *
     * @param newBookingDto - новое бронирование в виде DTO
     * @param userId        - пользователь, совершающий бронивароние
     * @return бронирование в виде DTO с вложенными Item
     */
    @PostMapping
    public BookingDtoWithItem postBooking(@Valid @RequestBody BookingDto newBookingDto, @RequestHeader("X-Sharer-User-Id") int userId) {
        BookingDtoWithItem bookingDto = bookingService.createBooking(newBookingDto, userId);
        log.info("Создано брониврание. Id = {}, user = {}, item = {}", bookingDto.getId(), userId, bookingDto.getItem().getId());
        return bookingDto;
    }

    /**
     * Изменение статуса бронирования владельцем вещи.
     *
     * @param bookingId - номер бронирования
     * @param approved  - подтверждение бронирования
     * @param userId    - пользователь, совершающий подтверждение. Должен быть владельцем вещи
     * @return бронирование в виде DTO с вложенными Item
     */
    @PatchMapping("/{bookingId}")
    public BookingDtoWithItem patchConfirmation(@PathVariable int bookingId, @RequestParam String approved, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Оновление статуса бронирования владельцем вещи. BookingId={}, UserId={}", bookingId, userId);
        return bookingService.updateBooking(bookingId, Boolean.parseBoolean(approved), userId);
    }

    /**
     * Запрос информации о бронировании
     *
     * @param bookingId - номер брониорования
     * @param userId    - пользователь, запрашивающий бронирование. Должен быть владельцем вещи или автором бронирования
     * @return - брониварование в виде DTO
     */
    @GetMapping("/{bookingId}")
    public BookingDtoWithItem getBooking(@PathVariable int bookingId, @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получено брониврование, BookingId={}, UserId={}", bookingId, userId);
        return bookingService.getBookingDtoById(bookingId, userId);
    }

    /**
     * Получение списка всех бронирований текущего пользователя (с постраничным просмотром)
     *
     * @param state  - фильтр по статусам бронирований. По умолчанию ALL
     * @param userId - id автора бронирований
     * @return -
     */
    @GetMapping()
    public List<BookingDtoWithItem> getAllByUser(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") int userId,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "3") int size) {
        log.info("Получены брониврования пользователя UserId={} со статусом {}", userId, state);
        return bookingService.getAllDtoByUserAndState(from, size, userId, state);
    }

    /**
     * Получение списка бронирований для всех вещей текущего пользователя (с постраничным просмотром)
     *
     * @param state  - фильтр по статусам бронирований. По умолчанию ALL
     * @param userId - id владельца вещей
     * @return - список бронирования в виде DTO
     */
    @GetMapping("/owner")
    public List<BookingDtoWithItem> getAllByItem(@RequestParam(value = "state", defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") int userId,
                                                 @RequestParam(name = "from", defaultValue = "0") int from,
                                                 @RequestParam(name = "size", defaultValue = "3") int size) {
        log.info("Получены брониврования пользователя UserId={} со статусом {}", userId, state);
        return bookingService.getAllDtoByOwnerAndState(from, size, userId, state);
    }
}
