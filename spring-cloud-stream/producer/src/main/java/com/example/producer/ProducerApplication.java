package com.example.producer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@EnableBinding(ProducerChannel.class)
@SpringBootApplication
public class ProducerApplication {

	private final MessageChannel consumer;

	public ProducerApplication(ProducerChannel producerChannel) {
		this.consumer = producerChannel.consumer();
	}

	@PostMapping("/greet/{name}")
	public void publish(@PathVariable String name){
		String greeting = "Hello, " + name;
		Message<String> msg = MessageBuilder.withPayload(greeting).build();
		this.consumer.send(msg);
	}

	public static void main(String[] args) {
		SpringApplication.run(ProducerApplication.class, args);
	}

}

interface ProducerChannel {

	@Output
	MessageChannel consumer();
}
