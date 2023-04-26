package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
public class Item {
    private int id; //id вещи
    private User owner; //владелец вещи
    private String name; //наименование
    private String description; //описание
    @JsonProperty("available")
    private Boolean isAvailable; //доступность вещи для аренды
    private ItemRequest request; //запрос, по которому была создана вещь
}
