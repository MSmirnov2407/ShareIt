package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemJpaRepository extends JpaRepository<Item, Integer> {
    List<Item> findAllByOwnerId(int ownerId, Pageable page);

    @Query("SELECT it " +
            "FROM Item it " +
            "WHERE (upper(it.name) like upper(concat('%',?1,'%')) " +
            "OR upper(it.description) like upper(concat('%',?1,'%'))) " +
            "AND it.isAvailable = true")
    List<Item> searchItems(String searchCondition, Pageable page);

    List<Item> findByRequest_IdIn(List<Integer> requestIds);
}
