package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<Booking, Integer> {

    List<Booking> findByBooker_IdAndEndIsBefore(int bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(int bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_Id(int bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStatus(int bookerId, Status state, Sort sort);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE (b.item.owner.id = ?1) " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwner(int ownerId);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE (b.item.owner.id = ?1) " +
            "AND (b.status = ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findByOwnerAndStatus(int ownerId, Status status);


    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE (b.item.owner.id = ?1) " +
            "AND (b.end < ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findPastByOwner(int ownerId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE (b.item.owner.id = ?1) " +
            "AND (b.start > ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findFutureByOwner(int ownerId, LocalDateTime now);

    @Query("SELECT b " +
            "FROM Booking AS b " +
            "WHERE (b.item.owner.id = ?1) " +
            "AND (b.start < ?2) " +
            "AND (b.end > ?2) " +
            "ORDER BY b.start DESC")
    List<Booking> findCurrentByOwner(int ownerId, LocalDateTime now);

    List<Booking> findByItem_IdIn(List<Integer> itemsIds, Sort sort);

    List<Booking> findByItem_IdAndStatus(int itemId, Status status, Sort sort);

    List<Booking> findByItem_idAndBooker_IdAndEndIsBefore(int itemId, int bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(int userId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItem_IdAndStartIsBeforeAndEndIsAfter(int itemId, LocalDateTime start, LocalDateTime end, Sort sort);

}
