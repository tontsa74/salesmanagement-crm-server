package com.github.s1ckcode.SalesManagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.persistence.EntityManager;
import java.time.LocalDate;

@SpringBootApplication
public class SalesManagementApplication implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

	@Autowired
	EventRepository eventRepository;

	public static void main(String[] args) {
		SpringApplication.run(SalesManagementApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		userRepository.save(new User("Jaska", LocalDate.now(),"Salesperson","Hash1"));
		userRepository.save(new User("Tintti",LocalDate.now(),"Office dog","Hash2"));

		eventRepository.save(new Event(userRepository.findByName("Jaska"), Event.CONTACT, "kontaktoitu"));
        eventRepository.save(new Event(userRepository.findByName("Jaska"), Event.SALE, "kauppa"));
        eventRepository.save(new Event(userRepository.findByName("Tintti"), Event.MEETING, "tapaaminen"));

        Iterable<Event> events = (eventRepository.findEventsByUser(userRepository.findByName("Jaska")));
        events.forEach((event -> System.out.println(event.getNotes())) );
	}
}