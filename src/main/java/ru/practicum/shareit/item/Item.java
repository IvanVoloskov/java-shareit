package ru.practicum.shareit.item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.User;

@Setter
@Getter
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    @Column(name = "name", nullable = false, length = 64)
    private String name;
    @Column(name = "description", nullable = false, length = 255)
    private String description;
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;
}