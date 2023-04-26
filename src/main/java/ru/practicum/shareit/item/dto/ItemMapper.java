package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {

    /**
     * Метод преобразования объекта в его Dto
     *
     * @param item - объект Item
     * @return - Dto объекта Item
     */
    public static ItemDto itemToDto(Item item) {
        ItemDto itemDto = new ItemDto(); //создали объект Dto

        itemDto.setId(item.getId()); // установили id
        itemDto.setIsAvailable(item.getIsAvailable());//установили isAvailable
        itemDto.setName(item.getName()); // установили Name
        itemDto.setDescription(item.getDescription()); // установили Description
        itemDto.setOwner(item.getOwner()); // установили Owner
        itemDto.setRequest(item.getRequest());

        return itemDto; //вернули Dto
    }

    /**
     * Обратное преобразование - DTO в объект
     *
     * @param itemDto - Dto объекта item
     * @return Item
     */
    public static Item dtoToItem(ItemDto itemDto) {
        Item item = new Item(); //создали объект Item

        item.setId(itemDto.getId()); // установили id
        item.setIsAvailable(itemDto.getIsAvailable());//установили isAvailable
        item.setName(itemDto.getName()); // установили Name
        item.setDescription(itemDto.getDescription()); // установили Description
        item.setOwner(itemDto.getOwner()); // установили Owner
        item.setRequest(itemDto.getRequest());

        return item; //вернули Item
    }
}
