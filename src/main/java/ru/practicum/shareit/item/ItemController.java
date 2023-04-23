package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    ItemService itemService;
    UserService userService;

    @Autowired
    public ItemController(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    @PostMapping
    public ItemDto postItem(@Valid @RequestBody ItemDto newItemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        Item item = ItemMapper.dtoToItem(newItemDto); //превращаем dto в объект
        item.setOwner(userService.getUserById(ownerId)); //присваиваем владельца (объект User вместо простого id)
        log.info("Создана вещь. Id = {}, name = {}", item.getId(), item.getName());
        return ItemMapper.itemToDto(itemService.createItem(item)); //возвращаем dto созданного объекта
    }

    @PatchMapping("/{itemId}")
    public ItemDto putItem(@RequestBody ItemDto itemDto, @PathVariable int itemId,
                           @RequestHeader("X-Sharer-User-Id") int ownerId) {
        Item item = ItemMapper.dtoToItem(itemDto); //превращаем dto в объект
        item.setId(itemId); //присваиваем id

        itemService.updateItem(item, ownerId); //обновляяем вещь В хранилище
        log.info("Обновлена вещь. Id = {}", item.getId());
        return ItemMapper.itemToDto(itemService.getItemById(itemId)); //возвращаем dto объекта, взятого по id
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Запрошена вещь. Id = {}", itemId);
        return ItemMapper.itemToDto(itemService.getItemById(itemId)); //превратили объект в dto;
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        List<Item> items = itemService.getAllByUser(ownerId);
        log.info("Получен список вещей пользователя с id= " + ownerId);
        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList()); //вернули список с преобразованием itemToDto
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        List<Item> items = itemService.searchItems(text); //получили список вещей по условию поиска
        log.info("Получен список вещей, содержащих text= " + text);

        return items.stream()
                .map(ItemMapper::itemToDto)
                .collect(Collectors.toList()); //вернули список с преобразованием itemToDto
    }
}
