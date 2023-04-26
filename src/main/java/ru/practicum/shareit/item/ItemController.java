package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto postItem(@Valid @RequestBody ItemDto newItemDto, @RequestHeader("X-Sharer-User-Id") int ownerId) {
        ItemDto itemDto = itemService.createItem(newItemDto, ownerId);
        log.info("Создана вещь. Id = {}, name = {}", itemDto.getId(), itemDto.getName());
        return itemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto putItem(@RequestBody ItemDto itemDto, @PathVariable int itemId,
                           @RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("Обновлена вещь. Id = {}", itemDto.getId());
        return itemService.updateItem(itemDto, itemId, ownerId); //обновляяем вещь В хранилище
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable int itemId) {
        log.info("Запрошена вещь. Id = {}", itemId);
        return itemService.getItemDtoById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("Получен список вещей пользователя с id= " + ownerId);
        return itemService.getAllDtoByUser(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен список вещей, содержащих text= " + text);
        return itemService.searchItemsDto(text);
    }
}
