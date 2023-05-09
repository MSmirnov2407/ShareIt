package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    /**
     * Получение Item из хранилища его id
     *
     * @param id - id вещи
     * @return Item - вещь
     */
    Item findItemById(int id);

    /**
     * Получение списка всех вещей
     *
     * @return
     */
    List<Item> getAllByUser(int ownerId);

    /**
     * Сохранение новой вещи в хранилище
     *
     * @param newItem сохраняемая вещь
     * @return сохраненная вещь
     */
    Item save(Item newItem);

    /**
     * Обновление вещи в хранилище
     *
     * @param updatedItem - обновленная вещь
     * @return обновленная вещь
     */
    Item updateItem(Item updatedItem);

    /**
     * УДаление вещи из хранилища
     *
     * @param id - id вещи
     */
    void deleteItemById(int id);

    /**
     * Поиск вещей по тексту в названии или описании
     *
     * @param text - искомый текст
     * @return
     */
    List<Item> searchItems(String text);
}
