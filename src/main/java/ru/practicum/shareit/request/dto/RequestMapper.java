package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.model.ItemRequest;

public class RequestMapper {
    /**
     * Метод преобразования объекта в его Dto
     *
     * @param itemRequest - объект Item
     * @return - Dto объекта Item
     */
    public static ItemRequestDto requestToDto(ItemRequest itemRequest) {
        ItemRequestDto requestDto = new ItemRequestDto(); //создали объект Dto

        requestDto.setId(itemRequest.getId()); // установили id
        requestDto.setRequestText(itemRequest.getRequestText());//установили RequestText
        requestDto.setCreated(itemRequest.getCreated());//установили Created
        requestDto.setRequestor(itemRequest.getRequestor());//установили Requestor

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
        request.setRequestText(itemRequestDto.getRequestText());//установили RequestText
        request.setCreated(itemRequestDto.getCreated());//установили Created
        request.setRequestor(itemRequestDto.getRequestor());//установили Requestor

        return request; //вернули ItemRequest
    }
}
