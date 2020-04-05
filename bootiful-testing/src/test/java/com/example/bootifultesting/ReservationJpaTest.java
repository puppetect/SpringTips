package com.example.bootifultesting;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ReservationJpaTest {

	@Autowired
	private TestEntityManager tem;
	@Test
	public void mapping(){
		Reservation bob = this.tem.persistFlushFind(new Reservation(null, "Bob"));
		Assertions.assertThat(bob.getReservationName()).isEqualTo("Bob");
		Assertions.assertThat(bob.getId()).isNotNull();
		Assertions.assertThat(bob.getId()).isGreaterThan(0);
	}
}
