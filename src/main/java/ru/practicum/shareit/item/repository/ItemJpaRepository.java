package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Integer> {
    List<Item> findByOwnerId(int ownerId);

    @Query("SELECT it " +
            "FROM Item it " +
            "WHERE (upper(it.name) like upper(concat('%',?1,'%')) " +
            "OR upper(it.description) like upper(concat('%',?1,'%'))) " +
            "AND it.isAvailable = true")
    List<Item> searchItems(String searchCondition);

}
