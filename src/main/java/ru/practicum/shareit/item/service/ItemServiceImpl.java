package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ElementNotFoundException;
import ru.practicum.shareit.exception.NotAnOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository; //хранилище вещей
    private final UserService userService;
    private int id; //id очередного создаваемого пользователя

    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository, UserService userService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        Item item = ItemMapper.dtoToItem(itemDto); //превращаем dto в объект
        item.setOwner(UserMapper.dtoToUser(userService.getUserDtoById(ownerId))); //присваиваем владельца (объект User вместо простого id)

        id++; //для каждой новой вещи инкрементируем id
        item.setId(id); //присваиваем id новой вещи
        itemRepository.saveItem(item); //сохраняем вещь в хранилище
        return ItemMapper.itemToDto(item); //возвращаем DTO Объекта
    }

    @Override
    public ItemDto getItemDtoById(int itemId) {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new ElementNotFoundException("Вещь с id = " + itemId + " не найдена.");
        }
        return ItemMapper.itemToDto(item); //вернули DTO Объекта
    }

    @Override
    public List<ItemDto> getAllDtoByUser(int ownerId) {
        List<Item> items = itemRepository.getAllByUser(ownerId); //взяли список вещей из репозитория
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList()); //вернули список с преобразованием itemToDto
    }

    @Override
    public void deleteItem(int id) {
        itemRepository.deleteItemById(id);
    }

    @Override
    public ItemDto updateItem(ItemDto updatedItemDto, int itemId, int ownerId) {
        Item item = ItemMapper.dtoToItem(updatedItemDto); //превращаем dto в объект
        item.setId(itemId); //присваиваем id
        validateUpdate(item, ownerId); //проверяем данные (помимо валидации аннотациями)
        itemRepository.updateItem(item);
        return ItemMapper.itemToDto(itemRepository.findItemById(item.getId())); //вернули DTO Объекта
    }

    @Override
    public List<ItemDto> searchItemsDto(String text) {
        String normalizedText = text.toLowerCase();
        List<Item> items = itemRepository.searchItems(normalizedText); //взяли список нужных вещей из хранилища
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList()); //вернули список с преобразованием itemToDto
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
