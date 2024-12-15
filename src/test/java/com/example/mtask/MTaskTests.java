package com.example.mtask;

import com.example.mtask.config.MinioTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(MinioTestConfig.class)
class MTaskTests {

	@Test
	void contextLoads() {
	}

}
