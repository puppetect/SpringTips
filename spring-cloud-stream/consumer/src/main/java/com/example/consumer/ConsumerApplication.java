package com.example.consumer;

import org.springframework.beans.factory.InjectionPoint;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.messaging.SubscribableChannel;

import java.util.logging.Logger;

@EnableBinding(ConsumerChannels.class)
@SpringBootApplication
public class ConsumerApplication {

	@Bean
	@Scope("prototype")
	Logger logger(InjectionPoint ip){
		return Logger.getLogger(ip.getDeclaredType().getName());
	}

	@Bean
	IntegrationFlow integrationFlow(Logger logger, ConsumerChannels c){
		return IntegrationFlows.from(c.producer())
				.handle(String.class, (payload, headers) -> {
					logger.info("new message:" + payload);
					return null;
				})
				.get();
	}
	public static void main(String[] args) {
		SpringApplication.run(ConsumerApplication.class, args);
	}

}

interface ConsumerChannels {
	@Input
	SubscribableChannel producer();
}
