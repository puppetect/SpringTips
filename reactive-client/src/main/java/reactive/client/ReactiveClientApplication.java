package reactive.client;

import lombok.extern.java.Log;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.reactive.function.client.WebClient;
import reactive.Event;
import reactive.service.ReactiveServiceApplication;

import java.util.Collections;

@Log
@SpringBootApplication
public class ReactiveClientApplication{

	public static void main(String[] args) {
		new SpringApplicationBuilder(ReactiveClientApplication.class)
				.properties(Collections.singletonMap("server.port", "8081"))
				.run(args);
	}

	@Bean
	WebClient client(){
		return WebClient.create("http://localhost:8080");
	}

	@Bean
	CommandLineRunner demo (WebClient client){
		log.info("running");
		return args -> client.get()
				.uri("/events")
				.accept(MediaType.TEXT_EVENT_STREAM)
				.retrieve()
				.bodyToFlux(Event.class)
				.subscribe(System.out::println);
	}

}
