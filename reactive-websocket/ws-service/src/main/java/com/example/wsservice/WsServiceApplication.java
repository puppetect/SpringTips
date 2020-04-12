package com.example.wsservice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.reactivestreams.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@SpringBootApplication
public class WsServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WsServiceApplication.class, args);
	}

}

@Configuration
class WebSockerConfiguration {

	@Bean
	public WebSocketHandlerAdapter wsha(){
		return new WebSocketHandlerAdapter();
	}
	@Bean
	public WebSocketHandler wsh() {
		return session -> {
			Publisher publisher = Flux.generate(Consumer< SynchronousSink<FileEvent>> {
				sink -> sink.next(FileEvent(System.currentTimeMillis(), "/a/b/c"))
			});
			session.send();
		};


		};
	}

	@Bean
	public HandlerMapping hm(){
		Map<String, Object> map = new HashMap<>();
		map.put("order", 10);
		map.put("urlMap", Collections.singletonMap("ws/files", wsh()));
		return new SimpleUrlHandlerMapping(map);
	}
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class FileEvent{
	String sessionId, path;

}
