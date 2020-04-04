package com.example.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class AnalyticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalyticsApplication.class, args);
	}

	@Component
	public static class PageViewEventSource implements ApplicationRunner {

		@Override
		public void run(ApplicationArguments args) throws Exception {

		}
	}

}

interface AnalyticsBinding {
	@Output
	MessageChannel output();
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class PageViewEvent {
	private String userId, page;
	private long duration;
}
