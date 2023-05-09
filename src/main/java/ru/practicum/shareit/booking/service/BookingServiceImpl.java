package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoWithItem;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingJpaRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(UserService userService, BookingJpaRepository bookingRepository, ItemService itemService) {
        this.userService = userService;
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
    }

    @Override
    public BookingDtoWithItem createBooking(BookingDto newBookingDto, int bookerId) {
        Booking booking = BookingMapper.dtoToBooking(newBookingDto); //превращаем dto в объект
        booking.setBooker(UserMapper.dtoToUser(userService.getUserDtoById(bookerId))); //присваиваем пользователя (объект User вместо простого id)
        booking.setItem(ItemMapper.dtoToItem(itemService.getItemDtoById(newBookingDto.getItemId()))); //присваиваем item (объект Item вместо простого id)

        validateCreate(booking); //дополнительная валидация
        bookingRepository.save(booking); //сохраняем бронирование в хранилище
        return BookingMapper.bookingToDtoWithItem(booking); //возвращаем DTO Объекта
    }

    @Override
    public BookingDtoWithItem getBookingDtoById(int bookingId, int userId) {
        Optional<Booking> booking = bookingRepository.findById(bookingId);
        if (booking.isEmpty()) {
            throw new ElementNotFoundException("Бронирование с id = " + bookingId + " не найдено.");
        }
        if ((booking.get().getItem().getOwner().getId() != userId) && (booking.get().getBooker().getId() != userId)) {
            throw new NotAnOwnerException("Пользователь с id=" + userId + " не является владельцем вещи или автором бронирования");
        }
        return BookingMapper.bookingToDtoWithItem(booking.get()); //вернули DTO Объекта
    }

    /**
     * Получение списка бронирований пользователя с фильтром по состоянию бронирования
     *
     * @param userId - Id пользователя
     * @param state  - состояние юронирования
     * @return список бронирований
     */
    @Override
    public List<BookingDtoWithItem> getAllDtoByUserAndState(int userId, String state) {
        UserDto user = userService.getUserDtoById(userId);
        if (user == null) {
            throw new ElementNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBooker_Id(userId, Sort.by("start").descending());
                break;
            case "PAST":
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, LocalDateTime.now(), Sort.by("start").descending());
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, LocalDateTime.now(), Sort.by("start").descending());
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), Sort.by("start").descending());
                break;
            case "WAITING":
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, Sort.by("start").descending());
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, Sort.by("start").descending());
                break;
            default:
                throw new StateNotAllowedException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDtoWithItem)
                .collect(Collectors.toList());
    }


    @Override
    public List<BookingDtoWithItem> getAllDtoByOwnerAndState(int userId, String state) {
        UserDto user = userService.getUserDtoById(userId);
        if (user == null) {
            throw new ElementNotFoundException("Пользователь с id=" + userId + " не найден");
        }
        List<Booking> bookings;
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByOwner(userId);
                break;
            case "PAST":
                bookings = bookingRepository.findPastByOwner(userId, LocalDateTime.now());
                break;
            case "FUTURE":
                bookings = bookingRepository.findFutureByOwner(userId, LocalDateTime.now());
                break;
            case "CURRENT":
                bookings = bookingRepository.findCurrentByOwner(userId, LocalDateTime.now());
                break;
            case "WAITING":
                bookings = bookingRepository.findByOwnerAndStatus(userId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByOwnerAndStatus(userId, Status.REJECTED);
                break;
            default:
                throw new StateNotAllowedException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDtoWithItem)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteBooking(int id) {

    }

    @Override
    public BookingDtoWithItem updateBooking(int bookingId, boolean approved, int userId) {
        Booking booking = bookingRepository.findById(bookingId).get(); //взяли из хранилища бронирование
        validateUpdate(booking, userId); //проверяем данные (помимо валидации аннотациями)

        if (approved) { //меняем статус бронирования в зависимости от значения approved
            if (booking.getStatus() == Status.APPROVED) { //если статус уже был установлен, то исключение
                throw new BookingAlreadyApprovedException("Бронирование с Id=" + booking.getId() + " уже было подтверждено ранее");
            }
            booking.setStatus(Status.APPROVED);
        } else {
            if (booking.getStatus() == Status.APPROVED) {
                throw new BookingAlreadyApprovedException("Бронирование с Id=" + booking.getId() + " уже было отклонено ранее");
            }
            booking.setStatus(Status.REJECTED);
        }
        bookingRepository.save(booking);
        return BookingMapper.bookingToDtoWithItem(bookingRepository.findById(bookingId).get()); //вернули DTO Объекта
    }

    private void validateUpdate(Booking booking, int userId) {
        if (booking == null) { //если бронирования с таким Id нет в хранилище - исключение
            throw new ElementNotFoundException("BookingService: брониврование с id=" + booking.getId() + " не найдено");
        }
        if (booking.getItem().getOwner().getId() != userId) { //если владелец не совпадает с переданным - исключение
            throw new NotAnOwnerException("BookingService: пользователь c id= " + userId +
                    " не является владельцем вещи c id=" + booking.getItem().getId());
        }
    }

    private void validateCreate(Booking booking) {
        LocalDateTime start = booking.getStart();
        LocalDateTime end = booking.getEnd();

        if (end.isBefore(start) || end.isBefore(LocalDateTime.now()) || end.isEqual(start)) {
            throw new BookingTimeException("Неверно указано время окончания бронирования");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new BookingTimeException("Неверно указано время начала бронирования");
        }
        if (!booking.getItem().getIsAvailable()) { //если вещь недоступна для бронирования - исключение
            throw new ItemNotAvailableException("BookingService: вещь с id= " + booking.getItem().getId() + " не доступна для бронирования");
        }
        if (booking.getItem().getOwner().getId() == booking.getBooker().getId()) { //если владелец совпадает с автором бронирования
            throw new BookerIsOwnerException("BookingService: автор бронирования является владельцем вещи");
        }

    }
}