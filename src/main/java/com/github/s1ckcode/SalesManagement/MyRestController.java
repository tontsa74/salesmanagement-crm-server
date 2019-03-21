package com.github.s1ckcode.SalesManagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.s1ckcode.SalesManagement.Event.Event;
import com.github.s1ckcode.SalesManagement.Event.EventRepository;
import com.github.s1ckcode.SalesManagement.Lead.Lead;
import com.github.s1ckcode.SalesManagement.Lead.LeadRepository;
import com.github.s1ckcode.SalesManagement.User.User;
import com.github.s1ckcode.SalesManagement.User.UserRepository;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class MyRestController {

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

    @DeleteMapping(value = "users/{userId}")
    public void deleteUser(@PathVariable int userId) {
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

    @PostMapping(value = "/events/add/{userId}/{leadId}")
    public void addEvent(@RequestBody Event event, @PathVariable int userId, @PathVariable int leadId) {
        event.setUser(userRepository.findById(userId).get());
        event.setLead(leadRepository.findById(leadId).get());
        eventRepository.save(event);
    }

    @GetMapping(value = "/events/{userId}")
    public Iterable<Event> getAllEventsFromUser(@PathVariable int userId) {
        return eventRepository.findEventsByUser(userRepository.findById(userId).get());
    }

    @GetMapping(value = "/events")
    public Iterable<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @PostMapping(value="/leads/add")
    public void addLead(@RequestBody Lead lead) {
        leadRepository.save(lead);
    }

    @GetMapping(value ="/leads")
    public Iterable<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

  //THIS IS THE "CORRECT" ONE  @GetMapping(value = "/companyData/{startDate}/{endDate}")
  @GetMapping(value = "/companyData/{sdd}/{sdm}/{sdy}/{edd}/{edm}/{edy}")
    public Iterable<JsonNode> getCompanyChart(@PathVariable int sdd,@PathVariable int sdm,@PathVariable int sdy,@PathVariable int edd,@PathVariable int edm,@PathVariable int edy){                  /// CORRECT ONE  @PathVariable LocalDate startDate, @PathVariable LocalDate endDate) {
        LocalDate startDate = LocalDate.of(sdy,sdm,sdd);
        LocalDate endDate = LocalDate.of(edy,edm,edd);
        return utils.getCompanyChartData(startDate, endDate);
    }
}
