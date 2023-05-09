package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Entity
@Table(name = "items", schema = "public")
@Getter
@Setter
@ToString
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; //id вещи
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner; //владелец вещи
    @Column(name = "name")
    private String name; //наименование
    @Column(name = "description")
    private String description; //описание
    @JsonProperty("available")
    @Column(name = "isAvailable")
    private Boolean isAvailable; //доступность вещи для аренды
    @OneToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request; //запрос, по которому была создана вещь
}
