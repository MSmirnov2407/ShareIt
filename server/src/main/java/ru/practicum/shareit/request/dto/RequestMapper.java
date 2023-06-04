package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

public class RequestMapper {
    /**
     * Метод преобразования объекта в его Dto
     *
     * @param itemRequest - объект запроса
     * @return - Dto запроса
     */
    public static ItemRequestDto requestToDto(ItemRequest itemRequest) {
        ItemRequestDto requestDto = new ItemRequestDto(); //создали объект Dto

        requestDto.setId(itemRequest.getId()); // установили id
        requestDto.setDescription(itemRequest.getDescription());//установили Description
        requestDto.setCreated(itemRequest.getCreated());//установили Created
        requestDto.setRequestor(UserMapper.userToDto(itemRequest.getRequestor()));//установили Requestor

        return requestDto; //вернули Dto
    }

    /**
     * Обратное преобразование DTO В объект
     *
     * @param itemRequestDto - DTO запроса
     * @return ItemRequest
     */
    public static ItemRequest dtoToRequest(ItemRequestDto itemRequestDto) {
        ItemRequest request = new ItemRequest(); //создали объект ItemRequest

        request.setId(itemRequestDto.getId()); // установили id
        request.setDescription(itemRequestDto.getDescription());//установили Description
        request.setCreated(itemRequestDto.getCreated());//установили Created
        request.setRequestor(UserMapper.dtoToUser(itemRequestDto.getRequestor()));//установили Requestor

        return request; //вернули ItemRequest
    }

    /**
     * Метод преобразования объекта в его Dto с вложенными ответами на запрос
     *
     * @param itemRequest - объект запроса
     * @return - Dto запроса с вложенным ответом
     */
    public static ItemRequestDtoWithAnswer requestToDtoWithAnswer(ItemRequest itemRequest, List<ItemDto> answers) {
        ItemRequestDtoWithAnswer requestDto = new ItemRequestDtoWithAnswer(); //создали объект Dto

        requestDto.setId(itemRequest.getId()); // установили id
        requestDto.setDescription(itemRequest.getDescription());//установили Description
        requestDto.setCreated(itemRequest.getCreated());//установили Created
        requestDto.setRequestor(UserMapper.userToDto(itemRequest.getRequestor()));//установили Requestor

        if (answers != null) { //тесты требуют не null, а пустой список
            requestDto.setItems(answers); //установили список ответов на запрос
        }

        return requestDto; //вернули Dto
    }
}
