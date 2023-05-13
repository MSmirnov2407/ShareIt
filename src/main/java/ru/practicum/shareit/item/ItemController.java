package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

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
    public ItemDtoWithBookings getItem(@PathVariable int itemId,
                                       @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Запрошена вещь. Id = {}", itemId);
        return itemService.getItemDtoWithBookingsById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoWithBookings> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") int ownerId) {
        log.info("Получен список вещей пользователя с id= " + ownerId);
        return itemService.getAllDtoByUser(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.info("Получен список вещей, содержащих text= " + text);
        return itemService.searchItemsDto(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable int itemId, @RequestHeader("X-Sharer-User-Id") int authorId,
                                    @Valid @RequestBody CommentDto commentDto) {
        CommentDto comment = itemService.createComment(itemId, authorId, commentDto);
        log.info("Оставлен комментарий от пользователя id={}, на вещь id= {}", itemId, authorId);
        return comment;
    }

}
