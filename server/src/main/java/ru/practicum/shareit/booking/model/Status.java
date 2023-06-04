package ru.practicum.shareit.booking.model;

public enum Status {
    WAITING, //новое бронирование
    APPROVED, //бронирование подтверждено владельцем
    REJECTED, //бронирование отклонено владельцем
    CANCELED //бронирование отменено создателем
}
