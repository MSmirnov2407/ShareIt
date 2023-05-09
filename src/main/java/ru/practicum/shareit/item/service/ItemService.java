package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import java.util.List;

public interface ItemService {


    /**
     * СОздание новой вещи
     *
     * @param newItemDto - новая вещь в виде DTO
     * @return - новая вещь, взятая из хранилища в виде DTO
     */
    ItemDto createItem(ItemDto newItemDto, int ownerId);

    /**
     * Получение DTO вещи из хранилища по id
     *
     * @param itemId
     * @return
     */
    ItemDto getItemDtoById(int itemId);

    /**
     * Получение DTO вещи c информацией о бронировании из хранилища по id
     *
     * @param itemId
     * @return
     */
    ItemDtoWithBookings getItemDtoWithBookingsById(int itemId, int userId);

    /**
     * Возвращение списка всех вещей
     *
     * @return
     */
    List<ItemDtoWithBookings> getAllDtoByUser(int ownerId);

    /**
     * Удаление вещи из хрангилища
     *
     * @param id
     */
    void deleteItem(int id);

    /**
     * Обновление вещи в хранилище
     *
     * @param updatedItemDto
     * @return
     */
    ItemDto updateItem(ItemDto updatedItemDto, int itemId, int ownerId);

    /**
     * Поиск вещи с совпадением текста в названии или описании
     *
     * @param text - искомый текст
     * @return
     */
    List<ItemDto> searchItemsDto(String text);

    /**
     * Добавление комментария от пользователя на вещь
     * @param itemId - id вещи
     * @param authorId - автор комментария
     * @return - комментарий
     */
    CommentDto createComment(int itemId, int authorId, CommentDto commentDto);
}
