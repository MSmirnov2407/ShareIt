package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private int id; //id вещи
    private User owner; //владелец вещи
    @NotBlank
    private String name; //наименование
    private String description; //описание
    @NotNull
    @JsonProperty("available")
    private Boolean isAvailable; //доступность вещи для аренды
    private ItemRequest request; //запрос, по которому была создана вещь
}
