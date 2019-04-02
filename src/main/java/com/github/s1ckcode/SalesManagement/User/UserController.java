package com.github.s1ckcode.SalesManagement.User;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.s1ckcode.SalesManagement.Event.Event;
import com.github.s1ckcode.SalesManagement.Event.EventRepository;
import com.github.s1ckcode.SalesManagement.Lead.LeadRepository;
import com.github.s1ckcode.SalesManagement.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    EventRepository eventRepository;
    @Autowired
    LeadRepository leadRepository;
    @Autowired
    Utils utils;

    @GetMapping(value = "/users")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping(value="/users/{userId}")
    public Optional<User> user(@PathVariable int userId) {
        return userRepository.findById(userId);
    }

    /*
    curl -H"Content-Type: application/json" -X POST -d {\"name\":\"mkyong\",\"role\":\"1\",\"password\":\"abc\"} http://localhost:8080/users/add
     */
    @PostMapping(value = "/users/add")
    public void addUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @DeleteMapping(value = "/users/{userId}")
    public void deleteUser(@PathVariable int userId) {
        Iterable<Event> events = eventRepository.findEventsByUser(userRepository.findById(userId).get());
        for(Event event:events) {
            if(event.getUser() != null) {
                event.setUser(null);
            }
        }
        userRepository.deleteById(userId);
    }

    @GetMapping(value = "/userData/{userId}")
    public JsonNode getUserData(@PathVariable int userId) {
        User user = userRepository.findById(userId).get();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.createObjectNode();
        ((ObjectNode) node).put("user_first",user.getUserFirst());
        ((ObjectNode) node).put("user_last",user.getUserLast());
        ((ObjectNode) node).put("hit_rate",utils.getHitrate(user));
        ((ObjectNode) node).put("avg_sales",utils.getAvgSales(user));
        ((ObjectNode) node).put("total_sales",utils.getAllSales(user));
        ((ObjectNode) node).put("contacts_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.CONTACT, user)).size());
        ((ObjectNode) node).put("meetings_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.MEETING, user)).size());
        ((ObjectNode) node).put("offers_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.OFFER, user)).size());
        ((ObjectNode) node).put("sales_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.SALE, user)).size());

        return node;
    }

    @GetMapping(value = "/userData/all")
    public Iterable<JsonNode> getAllUsersData() {
        ObjectMapper mapper = new ObjectMapper();
        Iterable<User> users = userRepository.findAll();
        List<JsonNode> entities= new ArrayList<>();

        for(User user: users) {
            JsonNode node = mapper.createObjectNode();
            ((ObjectNode) node).put("user_first",user.getUserFirst());
            ((ObjectNode) node).put("user_last",user.getUserLast());
            ((ObjectNode) node).put("hit_rate",utils.getHitrate(user));
            ((ObjectNode) node).put("avg_sales",utils.getAvgSales(user));
            ((ObjectNode) node).put("total_sales",utils.getAllSales(user));
            ((ObjectNode) node).put("contacts_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.CONTACT, user)).size());
            ((ObjectNode) node).put("meetings_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.MEETING, user)).size());
            ((ObjectNode) node).put("offers_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.OFFER, user)).size());
            ((ObjectNode) node).put("sales_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUser(Event.SALE, user)).size());
            entities.add(node);
        }

        return entities;
    }

    @GetMapping(value="/userData/all/{startDate}/{endDate}")
    public Iterable<JsonNode> getAllUsersDataBetween(@PathVariable String startDate, @PathVariable String endDate) {
        ObjectMapper mapper = new ObjectMapper();
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);
        Iterable<User> users = userRepository.findAll();
        List<JsonNode> entities = new ArrayList<>();

        for (User user: users) {
            JsonNode node = mapper.createObjectNode();
            ((ObjectNode) node).put("user_first",user.getUserFirst());
            ((ObjectNode) node).put("user_last",user.getUserLast());
            ((ObjectNode) node).put("hit_rate",utils.getHitrate(user, startLocalDate, endLocalDate));
            ((ObjectNode) node).put("avg_sales",utils.getAvgSales(user, startLocalDate, endLocalDate));
            ((ObjectNode) node).put("total_sales",utils.getAllSales(user, startLocalDate, endLocalDate));
            ((ObjectNode) node).put("contacts_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUserAndDateBetween(Event.CONTACT, user, startLocalDate, endLocalDate)).size());
            ((ObjectNode) node).put("meetings_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUserAndDateBetween(Event.MEETING, user, startLocalDate, endLocalDate)).size());
            ((ObjectNode) node).put("offers_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUserAndDateBetween(Event.OFFER, user, startLocalDate, endLocalDate)).size());
            ((ObjectNode) node).put("sales_amount", ((List<Event>)eventRepository.findEventsByEventTypeAndUserAndDateBetween(Event.SALE, user, startLocalDate, endLocalDate)).size());
            entities.add(node);
        }

        return entities;
    }

    @GetMapping(value = "/userEvents/{userId}")
    public Iterable<Iterable> getUserEvents(@PathVariable int userId) {
        Iterable<Iterable> userEvents = new ArrayList<>();

        User user = userRepository.findById(userId).get();

        ((ArrayList<Iterable>) userEvents).add(eventRepository.findEventsByEventTypeAndUser(Event.CONTACT,user));
        ((ArrayList<Iterable>) userEvents).add(eventRepository.findEventsByEventTypeAndUser(Event.MEETING,user));
        ((ArrayList<Iterable>) userEvents).add(eventRepository.findEventsByEventTypeAndUser(Event.OFFER,user));
        ((ArrayList<Iterable>) userEvents).add(eventRepository.findEventsByEventTypeAndUser(Event.SALE,user));
        ((ArrayList<Iterable>) userEvents).add(leadRepository.findAll());

        return userEvents;
    }
}
