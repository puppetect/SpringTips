package com.example.edgeservice;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.stream.IntStream;

@SpringBootApplication
public class EdgeServiceApplication {

	@Bean
	ApplicationRunner client(){
		return args -> {
			WebClient webClient = WebClient.builder().filter(ExchangeFilterFunctions.basicAuthentication("user", "pw")).build();
			Flux.fromStream(IntStream.range(0, 100).boxed())
					.flatMap(number -> webClient.get().uri("http://localhost:8081/rl").exchange())
					.flatMap(clientResponse -> clientResponse.toEntity(String.class).map(res -> String.format("status: %s, body: %s", res.getStatusCodeValue(), res.getBody())))
					.subscribe(System.out::println);
		};
	}

//	authentication
	@Bean
	MapReactiveUserDetailsService authentication(){
		return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder()
				.username("user")
				.password("pw")
				.roles("USER")
				.build()
		);
	}

//	authorization
	@Bean
	SecurityWebFilterChain authorization(ServerHttpSecurity security){
		return security
				.authorizeExchange().pathMatchers("/rl").authenticated()
				.anyExchange().permitAll()
				.and()
				.httpBasic()
				.and()
				.build();
	}


	@Bean
	RouteLocator gatewayRoutes(RouteLocatorBuilder builder){
		return builder.routes()
//				basic proxy
				.route(r -> r.path("/github")
						.uri("http://github.com")
						.id("method_route"))
//				load balanced proxy
				.route(r->r.path("/lb")
						.filters(f -> f.stripPrefix(1).prefixPath("/customers"))
						.uri("lb://CUSTOMER-SERVICE")
						.id("lb"))
//				custom filter 1
				.route(r -> r.path("/cf1")
						.filters(f -> f
								.stripPrefix(1).prefixPath("/customers")
								.setStatus(HttpStatus.CONFLICT)
								.setResponseHeader("Content-Type", MediaType.APPLICATION_PDF_VALUE))
						.uri("lb://CUSTOMER-SERVICE")
						.id("cf1"))
//				custom filter 2
				.route(r -> r.path("/cf2/**")
						.filters(f -> f.rewritePath("/cf2/(?<cid>.*)", "/customers/$\\{cid}"))
						.uri("lb://CUSTOMER-SERVICE")
						.id("cf2"))
//				circuit breaker
				.route(r -> r.path("/cb")
						.filters(f -> f
								.stripPrefix(1).prefixPath("/delay")
								.hystrix(c -> c.setName("cb")))
						.uri("lb://CUSTOMER-SERVICE")
						.id("cb"))
//				rate limiter
				.route(r -> r.path("/rl")
						.filters(f -> f.stripPrefix(1).prefixPath("/customers")
								.requestRateLimiter().rateLimiter(RedisRateLimiter.class, rl-> rl.setReplenishRate(5).setBurstCapacity(10)).and())
						.uri("lb://CUSTOMER-SERVICE")
						.id("rl"))
				.build();
	}
	public static void main(String[] args) {
		SpringApplication.run(EdgeServiceApplication.class, args);
	}

}
