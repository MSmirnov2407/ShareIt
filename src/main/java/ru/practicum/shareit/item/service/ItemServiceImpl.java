package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.NotAnOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository; //хранилище вещей

    private int id; //id очередного создаваемого пользователя

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item createItem(Item newItem) {
        id++; //для каждой новой вещи инкрементируем id
        newItem.setId(id); //присваиваем id новой вещи
        itemRepository.saveItem(newItem); //сохраняем вещь в хранилище
        return newItem;
    }

    @Override
    public Item getItemById(int itemId) {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new ElementNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        return item;
    }

    @Override
    public List<Item> getAllByUser(int ownerId) {
        return itemRepository.getAllByUser(ownerId);
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.deleteItemById(id);
    }

    @Override
    public Item updateItem(Item updatedItem, int ownerId) {
        validateUpdate(updatedItem, ownerId); //проверяем данные (помимо валидации аннотациями)
        itemRepository.updateItem(updatedItem);
        return itemRepository.findItemById(updatedItem.getId());
    }

    @Override
    public List<Item> searchItems(String text) {
        String normalizedText = text.toLowerCase();
        return itemRepository.searchItems(normalizedText);
    }

    private void validateUpdate(Item item, int ownerId) {
        Item storedItem = itemRepository.findItemById(item.getId()); //взяли вещь из хранилища
        if (storedItem == null) { //если вещи с таким Id нет в хранилище - исключение
            throw new ElementNotFoundException("ItemService: вещь с id=" + item.getId() + " не найдена");
        }
        if (storedItem.getOwner().getId() != ownerId) { //если владелец не совпадает с переданным - исключение
            throw new NotAnOwnerException("ItemService: указанный пользователь не является владельцем вещи");
        }
    }
}
