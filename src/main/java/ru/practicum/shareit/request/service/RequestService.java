package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithAnswer;

import java.util.List;

public interface RequestService {
    /**
     * СОздание нового запроса
     *
     * @param itemRequestDto - DTO Запроса
     * @param userId         - Id пользоавтеля, создавшего запрос
     * @return - DTO созданного запроса
     */
    ItemRequestDto createRequest(ItemRequestDto itemRequestDto, int userId);

    /**
     * Получение всех запросов поользователя с ответами на них. Сортированы От новых к старым
     *
     * @param userId - id пользователя
     * @return - список его запросов
     */
    List<ItemRequestDtoWithAnswer> getAllDtoWithAnswByUserId(int userId);

    /**
     * Получение списка запросов, созданных другими пользователями с постраничным выводом.
     *
     * @param from - начальный элемент для вывода ( с нуля)
     * @param size - количество элементов на страницу
     * @return список запросов
     */
    List<ItemRequestDtoWithAnswer> getAllDtoWithAnsw(int from, int size, int userID);

    /**
     * Получение данны[ об одном конкретном запросе вместе с данными об ответах
     *
     * @param requestId - id запроса
     * @param userId    - id пользовтеля, выгружающего запросы
     * @return - DTO Запроса с ответами на него
     */
    ItemRequestDtoWithAnswer getDtoWithAnswById(int requestId, int userId);
}
