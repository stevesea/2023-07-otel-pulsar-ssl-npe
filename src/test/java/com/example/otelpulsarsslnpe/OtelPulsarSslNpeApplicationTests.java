package com.example.otelpulsarsslnpe;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = "localhost-nossl")
class OtelPulsarSslNpeApplicationTests {

	@Test
	void contextLoads() {
	}

}
