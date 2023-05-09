package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    /**
     * Метод преобразования объекта в его Dto
     *
     * @param booking - объект Booking
     * @return - Dto объекта Item
     */
    public static BookingDto bookingToDto(Booking booking) {
        BookingDto bookingDto = new BookingDto(); //создали объект Dto
        /*устанавлилваем значения полей*/
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(booking.getBooker());

        return bookingDto; //вернули Dto
    }

    /**
     * Обратное преобразование DTO в объект
     *
     * @param bookingDto - DTO брониорвания
     * @return - Booking
     */
    public static Booking dtoToBooking(BookingDto bookingDto) {
        Booking booking = new Booking(); //создали объект Booking

        /*устанавлилваем значения полей*/
        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setStatus(bookingDto.getStatus());

        return booking; //вернули Booking
    }

    /**
     * Метод преобразования объекта в его Dto c вложенной информацией о вещи
     *
     * @param booking - объект Booking
     * @return - Dto объекта Item
     */
    public static BookingDtoWithItem bookingToDtoWithItem(Booking booking) {
        BookingDtoWithItem bookingDto = new BookingDtoWithItem(); //создали объект Dto
        /*устанавлилваем значения полей*/
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItem(booking.getItem());
        bookingDto.setBooker(booking.getBooker());

        bookingDto.setStatus(booking.getStatus());
        return bookingDto; //вернули Dto
    }

    /**
     * Метод преобразования объекта в его Dto, но вместо Пользоателя только его id
     *
     * @param booking - объект Booking
     * @return - Dto объекта Item
     */
    public static BookingDtoWithoutBooker bookingToDtoWithoutBooker(Booking booking) {
        BookingDtoWithoutBooker bookingDto = new BookingDtoWithoutBooker(); //создали объект Dto
        /*устанавлилваем значения полей*/
        bookingDto.setId(booking.getId());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setItemId(booking.getItem().getId());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBookerId(booking.getBooker().getId());

        return bookingDto; //вернули Dto
    }
}