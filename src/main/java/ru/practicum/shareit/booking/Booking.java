package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * // TODO .
 */
@AllArgsConstructor
@Data
public class Booking {
    Long id;
    Date start;
    Date end;
    Item item;
    User booker;
    String status;
}
