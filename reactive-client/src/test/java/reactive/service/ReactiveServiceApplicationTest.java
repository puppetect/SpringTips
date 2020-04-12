package reactive.service;

import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

@ExtendWith(SpringExtension.class)
@WebFluxTest(ReactiveServiceApplication.class)
@Log
class ReactiveServiceApplicationTest {

	@Autowired
	private WebTestClient webTestClient;

	@Test
	void eventById() {
		log.info("test========" + this.webTestClient);
		this.webTestClient
				.get()
				.uri("/events/42")
				.accept(MediaType.APPLICATION_JSON)
				.exchange()
				.expectStatus().isOk();
	}
}
