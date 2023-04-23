package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {


    /**
     * СОздание новой вещи
     *
     * @param newItem - новая вещь
     * @return - новая вещь, взятая из хранилища
     */
    Item createItem(Item newItem);

    /**
     * Получение вещи из хранилища по id
     *
     * @param itemId
     * @return
     */
    Item getItemById(int itemId);

    /**
     * Возвращение списка всех вещей
     *
     * @return
     */
    List<Item> getAllByUser(int ownerId);

    /**
     * Удаление вещи из хрангилища
     *
     * @param id
     */
    void deleteItem(int id);

    /**
     * Обновление вещи в хранилище
     *
     * @param updatedItem
     * @return
     */
    Item updateItem(Item updatedItem, int ownerId);

    /**
     * Поиск вещи с совпадением текста в названии или описании
     *
     * @param text - искомый текст
     * @return
     */
    List<Item> searchItems(String text);

}
