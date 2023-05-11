package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingJpaRepository;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.NotAnOwnerException;
import ru.practicum.shareit.exception.ValidateCommentException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentJpaRepository;
import ru.practicum.shareit.item.repository.ItemJpaRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemJpaRepository itemRepository; //хранилище вещей
    private final BookingJpaRepository bookingRepository; //хранилище бронирований

    private final CommentJpaRepository commentRepository; // хранилище комментариев

    private final UserService userService;

    @Autowired
    public ItemServiceImpl(ItemJpaRepository itemRepository, UserService userService, BookingJpaRepository bookingJpaRepository, CommentJpaRepository commentRepository) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.bookingRepository = bookingJpaRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        itemDto.setOwner(userService.getUserDtoById(ownerId)); //присваиваем владельца (объект User вместо простого id)
        Item item = ItemMapper.dtoToItem(itemDto); //превращаем dto в объект
        itemRepository.save(item); //сохраняем вещь в хранилище
        return ItemMapper.itemToDto(item); //возвращаем DTO Объекта
    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ElementNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        return ItemMapper.itemToDto(item.get()); //вернули DTO Объекта
    }

    @Override
    public ItemDtoWithBookings getItemDtoWithBookingsById(int itemId, int userId) {
        Optional<Item> item = itemRepository.findById(itemId);
        if (item.isEmpty()) {
            throw new ElementNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        List<Booking> bookings = bookingRepository.findByItem_IdAndStatus(itemId, Status.APPROVED, Sort.by("start").ascending()); //список всех бронирований, относящихся к item-ам от старых к новым
        List<Comment> comments = commentRepository.findByItem_Id(itemId, Sort.by("created").ascending()); //сортировка от старых к новым
        List<CommentDto> commentsDto = comments.stream().map(CommentMapper::commentToDto).collect(Collectors.toList()); //преобразовали комменты в DTO
        return ItemMapper.itemToDtoWithBookings(item.get(), bookings, userId, commentsDto); //вернули DTO Объекта
    }

    @Override
    public List<ItemDtoWithBookings> getAllDtoByUser(int ownerId) {
        List<Item> items = itemRepository.findByOwnerId(ownerId); //взяли список вещей из репозитория
        List<Integer> itemsIds = items.stream().map(Item::getId).collect(Collectors.toList()); // список id из списка item-ов

        List<Booking> bookings = bookingRepository.findByItem_IdIn(itemsIds, Sort.by("start").ascending()); //список всех бронирований, относящихся к item-ам от старых к новым
        List<Comment> comments = commentRepository.findByItem_IdIn(itemsIds, Sort.by("created").ascending()); //сортировка от старых к новым
        List<CommentDto> commentsDto = comments.stream().map(CommentMapper::commentToDto).collect(Collectors.toList()); //преобразовали комменты в DTO

        /*преобразование списков в мапы из id и бронирования/комментария */
        Map<Integer, List<Booking>> bookingsMap = itemsIds.stream()
                .collect(Collectors.toMap(itemId -> itemId,
                        i -> bookings.stream()
                                .filter(b -> b.getItem().getId() == i)
                                .collect(Collectors.toList())));
        Map<Integer, List<CommentDto>> commentsDtoMap = itemsIds.stream()
                .collect(Collectors.toMap(itemId -> itemId,
                        i -> commentsDto.stream()
                                .filter(c -> c.getItemId() == i)
                                .collect(Collectors.toList())));
        return items.stream().map(i -> ItemMapper.itemToDtoWithBookings(i, bookingsMap.get(i.getId()), ownerId, commentsDtoMap.get(i.getId()))).collect(Collectors.toList()); //вернули список с преобразованием itemToDto
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

    @Override
    public ItemDto updateItem(ItemDto updatedItemDto, int itemId, int ownerId) {
        Item updatedItem = ItemMapper.dtoToItem(updatedItemDto); //превращаем dto в объект
        updatedItem.setId(itemId); //присваиваем id
        Item item = itemRepository.findById(itemId).get(); //взяли вещь из хранилища
        validateUpdate(updatedItem, ownerId, item); //проверяем данные (помимо валидации аннотациями)

        String name = updatedItem.getName();
        String description = updatedItem.getDescription();
        Boolean available = updatedItem.getIsAvailable();

        /*обновление данных в вещи, при их наличии в updatedItem*/
        if (!(name == null || name.isBlank())) {
            item.setName(updatedItem.getName());
        }
        if (!(description == null || description.isBlank())) {
            item.setDescription(updatedItem.getDescription());
        }
        if (available != null) {
            item.setIsAvailable(updatedItem.getIsAvailable());
        }
        itemRepository.save(item);
        return ItemMapper.itemToDto(itemRepository.findById(itemId).get()); //вернули DTO Объекта
    }

    @Override
    public List<ItemDto> searchItemsDto(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        List<Item> items = itemRepository.searchItems(text); //взяли список нужных вещей из хранилища
        return items.stream().map(ItemMapper::itemToDto).collect(Collectors.toList()); //вернули список с преобразованием itemToDto
    }

    @Override
    public CommentDto createComment(int itemId, int authorId, CommentDto commentDto) {


        Comment comment = CommentMapper.dtoToComment(commentDto); //превращаем dto в объект
        comment.setCreated(LocalDateTime.now()); //присвоили время создания комментария
        comment.setAuthor(UserMapper.dtoToUser(userService.getUserDtoById(authorId))); //присваиваем автора
        comment.setItem(ItemMapper.dtoToItem(getItemDtoById(itemId))); //присваиваем Item

        validateComment(comment);

        commentRepository.save(comment); //сохраняем комментарий в хранилище

        return CommentMapper.commentToDto(comment);
    }

    private void validateUpdate(Item item, int ownerId, Item storedItem) {
        if (storedItem == null) { //если вещи с таким Id нет в хранилище - исключение
            throw new ElementNotFoundException("ItemService: вещь с id=" + item.getId() + " не найдена");
        }
        if (storedItem.getOwner().getId() != ownerId) { //если владелец не совпадает с переданным - исключение
            throw new NotAnOwnerException("ItemService: указанный пользователь не является владельцем вещи");
        }
    }

    private void validateComment(Comment comment) {
        int itemId = comment.getItem().getId();
        int userId = comment.getAuthor().getId();
        LocalDateTime commentTime = comment.getCreated();

        List<Booking> bookings = bookingRepository.findByItem_idAndBooker_IdAndEndIsBefore(itemId, userId, commentTime, Sort.by("start").descending());

        if (bookings.isEmpty()) { //если нет бронирований - исключение
            throw new ValidateCommentException("Пользователь не брал вещь в аренду или аренда еще не окончена");
        }
    }
}
