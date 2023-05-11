package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ItemMapper {

    /**
     * Метод преобразования объекта в его Dto
     *
     * @param item - объект Item
     * @return - Dto объекта Item
     */
    public static ItemDto itemToDto(Item item) {
        ItemDto itemDto = new ItemDto(); //создали объект Dto

        itemDto.setId(item.getId()); // установили id
        itemDto.setIsAvailable(item.getIsAvailable());//установили isAvailable
        itemDto.setName(item.getName()); // установили Name
        itemDto.setDescription(item.getDescription()); // установили Description
        itemDto.setOwner(UserMapper.userToDto(item.getOwner())); // установили Owner
        itemDto.setRequest(item.getRequest());

        return itemDto; //вернули Dto
    }

    /**
     * Обратное преобразование - DTO в объект
     *
     * @param itemDto - Dto объекта item
     * @return Item
     */
    public static Item dtoToItem(ItemDto itemDto) {
        Item item = new Item(); //создали объект Item

        item.setId(itemDto.getId()); // установили id
        item.setIsAvailable(itemDto.getIsAvailable());//установили isAvailable
        item.setName(itemDto.getName()); // установили Name
        item.setDescription(itemDto.getDescription()); // установили Description
        item.setOwner(UserMapper.dtoToUser(itemDto.getOwner())); // установили Owner
        item.setRequest(itemDto.getRequest());

        return item; //вернули Item
    }

    /**
     * Метод преобразования объекта в его Dto с указанием ближайших бронирований
     *
     * @param item     - объект Item
     * @param bookings - список бронивароений
     * @param userId   - пользователь, запрашивающий данные
     * @return - Dto объекта Item
     */
    public static ItemDtoWithBookings itemToDtoWithBookings(Item item, List<Booking> bookings, int userId, List<CommentDto> comments) {
        ItemDtoWithBookings itemDto = new ItemDtoWithBookings(); //создали объект Dto

        itemDto.setId(item.getId()); // установили id
        itemDto.setIsAvailable(item.getIsAvailable());//установили isAvailable
        itemDto.setName(item.getName()); // установили Name
        itemDto.setDescription(item.getDescription()); // установили Description
        itemDto.setOwner(UserMapper.userToDto(item.getOwner())); // установили Owner
        itemDto.setRequest(item.getRequest());
        itemDto.setComments(comments);

        /*поиск последнего и следущего бронирований.*/
        Optional<Booking> lastBookingOptional = bookings.stream()
                .filter(b -> b.getStart().isBefore(LocalDateTime.now())) //фильтруем по дате начала бронирования до текущего времени
                .min((b1, b2) -> b2.getStart()
                        .compareTo(b1.getStart())); //отсортировали по датам и взяли ближайшую к текущей
        Optional<Booking> nextBookingOptional = bookings.stream()
                .filter(b -> b.getStart().isAfter(LocalDateTime.now())) //фильтруем по дате начала бронирования после текущего времени
                .findFirst();//взяли первую дату, т.к. даты итак отсортированы при выборке из БД (от старых к новым)

        if (lastBookingOptional.isPresent() && (item.getOwner().getId() == userId)) {
            itemDto.setLastBooking(BookingMapper.bookingToDtoWithoutBooker(lastBookingOptional.get()));
        }
        if (nextBookingOptional.isPresent() && (item.getOwner().getId() == userId)) {
            itemDto.setNextBooking(BookingMapper.bookingToDtoWithoutBooker(nextBookingOptional.get()));
        }

        return itemDto; //вернули Dto
    }

}
