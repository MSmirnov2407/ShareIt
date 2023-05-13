package ru.practicum.shareit.booking.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings", schema = "public")
@Getter
@Setter
@ToString
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //номер бронирования
    @Column(name = "start_date")
    private LocalDateTime start; // дата начала аренды
    @Column(name = "end_date")
    private LocalDateTime end; // дата окончания аренды
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // вещи
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker; //пользователь, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    private Status status = Status.WAITING; //статус,подтверждено ли бронирование владельцем
}
