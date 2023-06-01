package ru.practicum.shareit.json;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json; //Вспомогательный объект для дальнейшей работы

    @Test
    @SneakyThrows
    void testItemDto() {

        ItemDto itemDto = new ItemDto();
        itemDto.setDescription("описание вещи");
        itemDto.setName("имя вещи");
        itemDto.setIsAvailable(true);
        itemDto.setId(33);
        itemDto.setRequestId(22);

        JsonContent<ItemDto> result = json.write(itemDto);  //преобразование в JSON

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId());
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDto.getIsAvailable());
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId());
    }
}
