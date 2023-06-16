package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface RequestJpaRepository extends JpaRepository<ItemRequest, Integer> {

    ItemRequest findById(int id);

    List<ItemRequest> findAllByRequestor_Id(int userId, Sort sort);

    List<ItemRequest> findAllByRequestor_IdNot(int userId, Pageable page);
}
