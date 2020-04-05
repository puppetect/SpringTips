package com.example.bootifultesting;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Collections;

@RestController
public class ReservationRestController {

	private final ReservationRepository reservationRepository;

	public ReservationRestController(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@GetMapping(value = "/reservations", produces = MediaType.APPLICATION_JSON_VALUE)
	Collection<Reservation> reservations(){
		return this.reservationRepository.findAll();
	}
}
