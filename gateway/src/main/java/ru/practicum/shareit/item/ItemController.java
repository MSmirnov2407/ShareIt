package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> postItem(@Valid @RequestBody ItemDto newItemDto,
                                           @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Creating item {}, userId={}", newItemDto, ownerId);
        return itemClient.postItem(newItemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> putItem(@RequestBody ItemDto itemDto, @PathVariable int itemId,
                                          @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.info("Update item id={}, userId={}", itemId, ownerId);
        return itemClient.putItem(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getUser(@PathVariable int itemId,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Get item, itemId={}, userId = {}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByUser(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Get items with userId={}, from={}, size={}", ownerId, from, size);
        return itemClient.getItems(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@NotBlank @RequestParam String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Search items with text={}, from={}, size={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable int itemId,
                                                @RequestHeader("X-Sharer-User-Id") int authorId,
                                                @Valid @RequestBody CommentDto commentDto){
        log.info("Creating comment {}, itemId={}, authorId={}",commentDto, itemId, authorId);
        return itemClient.postComment(commentDto, itemId, authorId);
    }

}
