package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Integer, Item> items = new HashMap<>(); //мапа id-вещь

    @Override
    public Item findItemById(int id) {
        return items.get(id); //вернули вещь по id
    }

    @Override
    public List<Item> getAllByUser(int ownerId) {
        return items.values().stream() //возвращаем значения мапы в виде списка с фильтром по владельцу
                .filter(i -> i.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item saveItem(Item newItem) {
        Integer id = newItem.getId(); //взяли id вещи
        items.put(id, newItem); //по id сложили в мапу новую вещь
        return items.get(id); //вернули вещь по id
    }

    @Override
    public Item updateItem(Item updatedItem) {
        Integer id = updatedItem.getId(); //взяли id вещи
        Item item = items.get(id); //взяли из хранилища вещь по этому id

        String name = updatedItem.getName();
        String description = updatedItem.getDescription();
        Boolean available = updatedItem.getIsAvailable();

        /*обновление данных в вещи, при их наличии в updatedItem*/
        if (!(name == null || name.isBlank())) {
            item.setName(updatedItem.getName());
        }
        if (!(description == null || description.isBlank())) {
            item.setDescription(updatedItem.getDescription());
        }
        if (available != null) {
            item.setIsAvailable(updatedItem.getIsAvailable());
        }

        items.put(id, item); //по id сложили в мапу обновленную вещь
        return items.get(id); //вернули вещь по id
    }

    @Override
    public void deleteItemById(int id) {
        items.remove(id);
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> emptyResult = new ArrayList<>();
        if (text.isBlank()) { //если поисковый запрос пуст, возырщаем пустой список
            return emptyResult;
        }
        return items.values().stream()
                .filter(Item::getIsAvailable)
                .filter(i -> (i.getName().toLowerCase().contains(text)
                        || i.getDescription().toLowerCase().contains(text))) //имя или описание содержат текст
                .collect(Collectors.toList());
    }
}
