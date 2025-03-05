package com.learning.journalApp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.learning.journalApp.client.WeatherClient;

@SpringBootTest
class JournalAppApplicationTests {

	@MockBean
	private WeatherClient weatherClient;

	@Test
	void contextLoads() {
	}

}
