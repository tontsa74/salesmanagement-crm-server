package com.github.s1ckcode.SalesManagement.Event;

import com.github.s1ckcode.SalesManagement.User.User;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

public interface EventRepository extends CrudRepository<Event,Integer> {
    public Iterable<Event> findEventsByUser(User user);
    public Iterable<Event> findEventsByEventTypeAndUser(int eventType, User user);
    public Iterable<Event> findEventsByDateBetween(LocalDate startDate, LocalDate endDate);
    //public Iterable<Event> findEventsByDateMonth(Month month);
}
