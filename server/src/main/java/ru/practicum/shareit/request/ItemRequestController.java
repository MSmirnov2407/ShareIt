package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
public class ItemRequestController {

    private final RequestService requestService;

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader("X-Sharer-User-Id") int userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Создан запрос от пользователя с id={}", userId);
        return requestService.createRequest(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDtoWithAnswer> getRequest(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Получен список запросов пользователя с id= {}", userId);
        return requestService.getAllDtoWithAnswByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithAnswer> getAllRequests(@RequestHeader("X-Sharer-User-Id") int userId,
                                                         @RequestParam(name = "from", defaultValue = "0") int from,
                                                         @RequestParam(name = "size", defaultValue = "3") int size) {
        log.info("Получены все запросы (одна страница from {} size {})", from, size);
        return requestService.getAllDtoWithAnsw(from, size, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithAnswer getRequestById(@RequestHeader("X-Sharer-User-Id") int userId,
                                                   @PathVariable int requestId) {
        log.info("Получен запрос c id = {}", requestId);
        return requestService.getDtoWithAnswById(requestId, userId);
    }

}
