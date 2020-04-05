package com.example.bootifultesting;
import org.junit.Assert;
import org.junit.Test;


public class ReservationTest {
	@Test
	public void creation(){
		Reservation r = new Reservation(1L, "Bob");
		Assert.assertEquals(r.getId(), (Long)1L);
	}
}
